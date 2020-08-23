import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Bot {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    Vertx vertx = Vertx.vertx();
    public static String link = "https://khl.api.webcaster.pro/api/khl_mobile/events_v2.json?page=44";

    String timeFormatter(Long unixTime) {

        String formattedTime = Instant.ofEpochSecond(unixTime)
                .atZone(ZoneId.of("GMT+0"))
                .format(formatter);
        return formattedTime;
    }

    public void getGames() throws IOException {

        String tokenString = Files.readString(Paths.get("src/main/resources/token.txt"), StandardCharsets.UTF_8);
        TelegramBot bot = new TelegramBot(tokenString);

        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                if (update.message().text().equals("/games")) {
                    try {
                        List<Games> gamesList = jsonRequest();
                        Collections.sort(gamesList, Games.COMPARE_BY_DAY); // sorting by UNIX time
                        String output = gamesList
                                .stream()
                                .map(list ->
                                        "Игра: " + list.event.name + "\n" +
                                                "Счёт: " + list.event.score + "\n" +
                                                "Место: " + list.event.location + "\n" +
                                                "Время: " + timeFormatter(list.event.startAtDay) + "\n")
                                .collect(Collectors.joining("\n"));
                        bot.execute(new SendMessage(update.message().chat().id(),
                                output));
                    } catch (JsonProcessingException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public List<Games> jsonRequest() throws InterruptedException, JsonProcessingException {

        WebClient client = WebClient.create(vertx);
        Buffer buffer = Buffer.buffer();

        client.requestAbs(HttpMethod.GET, link)
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        HttpResponse<Buffer> response = asyncResult.result();
                        //System.out.println(response.bodyAsString());
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
        System.out.println("Client is closed");

        ObjectMapper mapper = new ObjectMapper();
        List<Games> games = mapper.readValue(buffer.toString(), new TypeReference<List<Games>>(){}); // getting list of current games

        return games;
    }

    public void pageToTimeMapper() {

        for(int i = 0; i < 44; i++) {

        }
    }
}
