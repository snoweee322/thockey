import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Bot{

    private final DateTimeFormatter formatterD = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter formatterT = DateTimeFormatter.ofPattern("HH:mm");

    Vertx vertx = Vertx.vertx();
    public static String link = "https://khl.api.webcaster.pro/api/khl_mobile/events_v2.json?page=";

    private String timeFormatter(Long unixTime, int type) { // 0 - day, 1 - time

        if(type == 0) {
            return Instant.ofEpochSecond(unixTime)
                    .atZone(ZoneId.of("GMT+3"))
                    .format(formatterD);
        } else {
            return Instant.ofEpochSecond(unixTime)
                    .atZone(ZoneId.of("GMT+3"))
                    .format(formatterT);
        }
    }

    public void getGames() throws IOException {

        String tokenString = Files.readString(Paths.get("src/main/resources/token.txt"), StandardCharsets.UTF_8);
        TelegramBot bot = new TelegramBot(tokenString);

        bot.setUpdatesListener(updates -> {

            updates.forEach(update -> {
                try {
                if (update.message().text().equals("/games@schedule_khl_bot") || update.message().text().equals("/games")) {
                    try {
                        List<Games> gamesList = scoreUpdate();
                        gamesList.sort(Games.COMPARE_BY_TIME); // sorting by UNIX time
                        String output = gamesList
                                .stream()
                                .map(list ->
                                        list.event.name + "\n" +
                                                "Счёт: " + list.event.score + "\n" +
                                                "Начало: " + timeFormatter(list.event.startAt/1000, 1) + "\n")
                                .collect(Collectors.joining("\n"));
                        bot.execute(new SendMessage(update.message().chat().id(),
                                "*Игровой день:\n" + timeFormatter(gamesList.get(0).event.startAtDay, 0) + "*\n")
                        .parseMode(ParseMode.Markdown));
                        bot.execute(new SendMessage(update.message().chat().id(), output));
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        System.err.println("ERROR: >>> " + e.getMessage());
                    }
                }
            } catch (NullPointerException e) {
                    e.printStackTrace();
                    System.err.println("ERROR: >>> " + e.getMessage());
                }
        });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private List<Games> jsonRequest(int pageNumber, Long time) throws InterruptedException, JsonProcessingException {

        WebClient client = WebClient.create(vertx);
        Buffer buffer = Buffer.buffer();

        client.requestAbs(HttpMethod.GET, link + pageNumber)
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        HttpResponse<Buffer> response = asyncResult.result();
                        System.out.println(response.bodyAsString());
                        buffer.appendString(response.bodyAsString());
                        System.out.println("Received response with status code " + response.statusCode());
                    }
                    else {
                        System.out.println("Something went wrong " + asyncResult.cause().getMessage());
                    }
                });

        while(buffer.length() == 0) {
            Thread.sleep(10);
        }
        client.close();

        ObjectMapper mapper = new ObjectMapper();
        List<Games> gamesFromPages = mapper.readValue(buffer.toString(), new TypeReference<>(){}); // getting list of current games

        return gamesFromPages.stream().filter(a -> a.event.startAtDay.equals(time)).collect(Collectors.toList());
    }

    private List<Games> jsonRequest(int pageNumber) throws InterruptedException, JsonProcessingException {

        WebClient client = WebClient.create(vertx);
        Buffer buffer = Buffer.buffer();

        client.requestAbs(HttpMethod.GET, link + pageNumber)
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        HttpResponse<Buffer> response = asyncResult.result();
                        System.out.println(response.bodyAsString());
                        buffer.appendString(response.bodyAsString());
                        System.out.println("Received response with status code " + response.statusCode());
                    }
                    else {
                        System.out.println("Something went wrong " + asyncResult.cause().getMessage());
                    }
                });

        while(buffer.length() == 0) {
            Thread.sleep(10);
        }
        client.close();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(buffer.toString(), new TypeReference<>(){});
    }

    public void saveToJson() throws IOException, InterruptedException {

        ArrayList<Games> allGames = new ArrayList<>();
        for(int i = 1; i <= 44; i++) {   // 1 - 44 inc.
            List<Games> temp = jsonRequest(i);
            for(int a = 0; a < temp.size(); a++) {
                temp.get(a).event.PAGE = i;
            }
            allGames.addAll(temp);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Paths.get("src/main/resources/schedule.json").toFile(), allGames);

        System.out.println("Total games: " + allGames.size());
        allGames.forEach(games -> System.out.println("\nGame: " + games.event.name +
                "\nScore: " + games.event.score +
                "\nLocation: " + games.event.location +
                "\nDate: " + timeFormatter(games.event.startAtDay, 0) +
                "\nTime: " + timeFormatter(games.event.startAt/1000, 1)
        ));
    }

    public ArrayList<Games> scoreUpdate() throws IOException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        List<Games> list = mapper.readValue(Paths.get("src/main/resources/schedule.json").toFile(), new TypeReference<>(){}); // берём кэшированный список, для точечного
                                                                                                                                              // обращения к серверу (обновляем счёт)
        TreeSet<Long> setOfTimes = new TreeSet<>();
        list.forEach(a -> setOfTimes.add(a.event.startAtDay)); // дерево времён начала игр (даты) GMT+3 MSK

        Long unixTimestamp = Instant.now().getEpochSecond();
        //Long unixTimestamp = 1614373200L;
        Long time;

        ArrayList<Games> games = new ArrayList<>();
        if (Long.compare(unixTimestamp, setOfTimes.first()) <= 0) { // если сезон ещё не начался (или плей-офф)

            time = setOfTimes.first();
            List<Games> gamesAtCertainTime = list.stream().filter(a -> a.event.startAtDay.equals(time)).collect(Collectors.toList()); // собираем игры по нужному времени
            Set<Integer> pagesToFind = gamesAtCertainTime.stream().map(a -> a.event.PAGE).collect(Collectors.toSet()); // берём необходимые адреса страниц с играми
            ArrayList<Integer> ptf = new ArrayList<>(pagesToFind);
            for(int i = 0; i < pagesToFind.size(); i++) {

                games.addAll(jsonRequest(ptf.get(i), time)); // обращение с передачей страниц и нужного времени
            }
        }
        else if (Long.compare(unixTimestamp, setOfTimes.first()) > 0 && Long.compare(unixTimestamp, setOfTimes.last()) < 0) { // если игра среди сезона
                                                                                                                              // между какими датами лежит время (если сезон уже идёт, а не до/после!)
            TreeSet<Long> subSetOfTimes = (TreeSet<Long>) setOfTimes.subSet(unixTimestamp - 86400, unixTimestamp + 86400);
            if ((Math.abs(unixTimestamp - subSetOfTimes.first())) <= (Math.abs(unixTimestamp - subSetOfTimes.last()))) // выбираем к какой дате ближе
                time = subSetOfTimes.first();
            else
                time = subSetOfTimes.last();
            List<Games> gamesAtCertainTime = list.stream().filter(a -> a.event.startAtDay.equals(time)).collect(Collectors.toList()); // собираем игры по нужному времени
            Set<Integer> pagesToFind = gamesAtCertainTime.stream().map(a -> a.event.PAGE).collect(Collectors.toSet()); // берём необходимые адреса страниц с играми
            ArrayList<Integer> ptf = new ArrayList<>(pagesToFind);
            for(int i = 0; i < pagesToFind.size(); i++) {
                games.addAll(jsonRequest(ptf.get(i), time)); // обращение с передачей страниц и нужного времени
            }
        }
        else {
            time = setOfTimes.last();
            List<Games> gamesAtCertainTime = list.stream().filter(a -> a.event.startAtDay.equals(time)).collect(Collectors.toList()); // собираем игры по нужному времени
            Set<Integer> pagesToFind = gamesAtCertainTime.stream().map(a -> a.event.PAGE).collect(Collectors.toSet()); // берём необходимые адреса страниц с играми
            ArrayList<Integer> ptf = new ArrayList<>(pagesToFind);
            for(int i = 0; i < pagesToFind.size(); i++) {
                games.addAll(jsonRequest(ptf.get(i), time)); // обращение с передачей страниц и нужного времени
            }
        }
        return games;
    }
}
