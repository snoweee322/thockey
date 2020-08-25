import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Requester {

    public static String linkG = "https://khl.api.webcaster.pro/api/khl_mobile/events_v2.json?order_direction=asc&q[start_at_gt_time_from_unixtime]=";
    public static String linkLast = "https://khl.api.webcaster.pro/api/khl_mobile/events_v2.json";

    private DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

    private Long unixTimestamp;
    private Vertx vertx;
    private ObjectMapper mapper;

    Requester(Vertx vertx) {

        this.unixTimestamp = Instant.now().getEpochSecond();
        this.vertx = vertx;
        this.mapper = new ObjectMapper();
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

    public ArrayList<Games> scoreUpdate() throws IOException, InterruptedException {

        List<Games> games = jsonRequest(Instant.now().getEpochSecond());
        return (ArrayList<Games>) games;
    }

    private List<Games> jsonRequest(Long unixTimestamp) throws InterruptedException, JsonProcessingException {

        WebClient client = WebClient.create(vertx);
        Buffer buffer = Buffer.buffer();

        client.requestAbs(HttpMethod.GET, linkG + unixTimestamp)
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        buffer.appendString(asyncResult.result().bodyAsString());
                        System.out.println("Received response with status code " + asyncResult.result().statusCode());
                    } else {
                        System.out.println("Something went wrong " + asyncResult.cause().getMessage());
                    }
                });

        while (buffer.length() == 0) {
            Thread.sleep(10);
        }

        client.close();
        List<Games> gamesFromPages = mapper.readValue(buffer.toString(), new TypeReference<>(){}); // getting list of current games
        if (gamesFromPages.isEmpty()) {
            return jsonRequestLast();
        }
        Long firstTime = gamesFromPages.get(0).event.startAtDay; // получаем первое время

        return gamesFromPages.stream().filter(a -> a.event.startAtDay.equals(firstTime)).collect(Collectors.toList());
    }

    protected List<Games> jsonRequestLast() throws InterruptedException, JsonProcessingException {

        WebClient client = WebClient.create(vertx);
        Buffer buffer = Buffer.buffer();

        client.requestAbs(HttpMethod.GET, linkLast)
                .send(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        HttpResponse<Buffer> response = asyncResult.result();
                        System.out.println(response.bodyAsString());
                        buffer.appendString(response.bodyAsString());
                        System.out.println("Received response with status code " + response.statusCode());
                    } else {
                        System.out.println("Something went wrong " + asyncResult.cause().getMessage());
                    }
                });

        while (buffer.length() == 0) {
            Thread.sleep(10);
        }
        client.close();
        ObjectMapper mapper = new ObjectMapper();
        List<Games> gamesOnLastPage = mapper.readValue(buffer.toString(), new TypeReference<>(){});
        Long lastGameTime = gamesOnLastPage.get(0).event.startAtDay;
        return gamesOnLastPage.stream().filter(a -> a.event.startAtDay.equals(lastGameTime)).collect(Collectors.toList());
    }
}
