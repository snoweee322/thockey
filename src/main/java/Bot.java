import JsonKHL.Games;
import JsonNHL.NHLStats;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm").withZone(ZoneId.of("GMT+3"));
    private final DateTimeFormatter europeanDateFormatterNoTime = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.of("GMT+3"));
    private final DateTimeFormatter ISOTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("GMT+0"));
    private final DateTimeFormatter ISOTimeGMT3 = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("GMT+3"));

    private static String linkLast = "https://khl.api.webcaster.pro/api/khl_mobile/events_v2.json";
    private static String linkG = "https://khl.api.webcaster.pro/api/khl_mobile/events_v2.json?order_direction=asc&q[start_at_gt_time_from_unixtime]=";
    private static String linkNHL = "https://statsapi.web.nhl.com/api/v1/schedule";

    public void getGames() throws IOException {

        String tokenString = Files.readString(Paths.get("src/main/resources/token.txt"), StandardCharsets.UTF_8);
        TelegramBot bot = new TelegramBot(tokenString);

        bot.setUpdatesListener(updates -> {

            updates.forEach(update -> {
                try {
                if (update.message().text().equals("/khl@schedule_khl_bot") || update.message().text().equals("/khl")) {

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Long lastGameTime;
                        List<Games> gamesOnPage = mapper.readValue(new URL(linkG + Instant.now().getEpochSecond()), new TypeReference<>(){});
                        if (gamesOnPage.isEmpty()) {
                            gamesOnPage = mapper.readValue(new URL(linkLast + Instant.now().getEpochSecond()), new TypeReference<>(){});
                            lastGameTime = gamesOnPage.get(0).event.startAtDay;
                            gamesOnPage = gamesOnPage.stream().filter(a -> a.event.startAtDay.equals(lastGameTime)).collect(Collectors.toList());
                        }
                        Long firstTime = gamesOnPage.get(0).event.startAtDay;
                        gamesOnPage.stream().filter(a -> a.event.startAtDay.equals(firstTime)).collect(Collectors.toList());
                        List<Games> gamesList = gamesOnPage.stream().filter(a -> a.event.startAtDay.equals(firstTime)).collect(Collectors.toList());
                        gamesList.sort(Games.COMPARE_BY_TIME); // sorting by UNIX time

                        StringBuilder outputString = new StringBuilder("*Игровой день: ")
                                .append(timeFormatter(gamesList.get(0).event.startAtDay, 0))
                                .append("*\n\n");
                        gamesList.forEach(list ->
                                outputString.append(list.event.name).append("\n")
                                            .append("Счёт: ").append(list.event.score).append("\n")
                                            .append("Начало: ").append(timeFormatter(list.event.startAt/1000, 1)).append("\n")
                        );
                        bot.execute(new SendMessage(update.message().chat().id(),
                                outputString.toString())
                                .parseMode(ParseMode.Markdown));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("ERROR: >>> " + e.getMessage());
                    }
                } else if (update.message().text().equals("/nhl@schedule_khl_bot") || update.message().text().equals("/nhl")) {

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        StringBuilder outputString = new StringBuilder();
                        NHLStats nhlStats;
                        if (Instant.now().getEpochSecond()%86400 > 36000) {
                            System.out.println("after 14:00");
                            nhlStats = mapper.readValue(new URL(linkNHL + "?date=" + ISOTimeGMT3.format(Instant.now())), NHLStats.class); // после 14:00 по GMT+3 перключается расписание
                        } else {
                            nhlStats = mapper.readValue(new URL(linkNHL), NHLStats.class);
                        }
                        int lastIdx = nhlStats.dates.get(0).games.size();

                        if(europeanDateFormatterNoTime.format(ISOTime.parse(nhlStats.dates.get(0).games.get(0).gameDate))
                                .equals(europeanDateFormatterNoTime.format(ISOTime.parse(nhlStats.dates.get(0).games.get(lastIdx-1).gameDate)))) { // переходящий игровой день либо один

                            outputString
                                    .append("*Игровой день: ")
                                    .append(DateTimeFormatter.ofPattern("dd.MM.yy")
                                            .format(ISOTime.parse(nhlStats.dates.get(0).games.get(0).gameDate)))
                                    .append("*\n\n");

                        } else {

                            outputString
                                    .append("*Игровой день: ")
                                    .append(DateTimeFormatter.ofPattern("dd.MM'—'")
                                            .format(ISOTime.parse(nhlStats.dates.get(0).games.get(0).gameDate)))
                                    .append(DateTimeFormatter.ofPattern("dd.MM.yy")
                                            .format(ISOTime.parse(nhlStats.dates.get(0).games.get(lastIdx-1).gameDate)))
                                    .append("*\n\n");
                        }

                        nhlStats.dates.get(0).games.stream().forEach(a ->
                                outputString
                                        .append(a.teams.away.team.name).append(" — ").append(a.teams.home.team.name)
                                        .append("\n")
                                        .append("Счёт: ").append(a.teams.away.score).append(":").append(a.teams.home.score)
                                        .append("\n")
                                        .append("Начало: ")
                                        .append(europeanDateFormatter.format(ISOTime.parse(a.gameDate)))
                                        .append("\n\n")
                        );
                        bot.execute(new SendMessage(update.message().chat().id(), outputString.toString())
                                .parseMode(ParseMode.Markdown));
                    } catch (IOException e) {
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

    protected String timeFormatter(Long unixTime, int type) { // 0 - date, 1 - time

        if (type == 0) {
            return Instant.ofEpochSecond(unixTime)
                    .atZone(ZoneId.of("GMT+3"))
                    .format(formatterDate);
        } else {
            return Instant.ofEpochSecond(unixTime)
                    .atZone(ZoneId.of("GMT+3"))
                    .format(formatterTime);
        }
    }
}
