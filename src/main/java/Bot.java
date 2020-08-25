import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import io.vertx.core.Vertx;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    public void getGames() throws IOException {

        String tokenString = Files.readString(Paths.get("src/main/resources/token.txt"), StandardCharsets.UTF_8);
        TelegramBot bot = new TelegramBot(tokenString);

        Vertx vertx = Vertx.vertx();

        bot.setUpdatesListener(updates -> {

            updates.forEach(update -> {
                try {
                if (update.message().text().equals("/games@schedule_khl_bot") || update.message().text().equals("/games")) {
                    try {
                        Requester requester = new Requester(vertx);
                        List<Games> gamesList = requester.scoreUpdate();
                        gamesList.sort(Games.COMPARE_BY_TIME); // sorting by UNIX time
                        String output = gamesList
                                .stream()
                                .map(list ->
                                        list.event.name + "\n" +
                                                "Счёт: " + list.event.score + "\n" +
                                                "Начало: " + requester.timeFormatter(list.event.startAt/1000, 1) + "\n")
                                .collect(Collectors.joining("\n"));
                        bot.execute(new SendMessage(update.message().chat().id(),
                                "*Игровой день:\n" + requester.timeFormatter(gamesList.get(0).event.startAtDay, 0) + "*\n")
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
}
