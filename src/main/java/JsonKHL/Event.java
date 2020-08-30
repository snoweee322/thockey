package JsonKHL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "game_state_key",
    "period",
    "hd",
    "id",
    "stage_id",
    "type_id",
    "commentator",
    "cola_company",
    "khl_id",
    "team_a",
    "team_b",
    "name",
    "tickets",
    "location",
    "start_at_day",
    "start_at",
    "event_start_at",
    "end_at",
    "not_regular",
    "score",
    "scores",
    "sscore",
    "image",
    "has_video",
    "m3u8_url",
    "feed_url"
})
public class Event {

    @JsonProperty("game_state_key")
    public String gameStateKey;
    @JsonProperty("period")
    public Integer period;
    @JsonProperty("hd")
    public Boolean hd;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("stage_id")
    public Long stageId;
    @JsonProperty("type_id")
    public Long typeId;
    @JsonProperty("commentator")
    public Boolean commentator;
    @JsonProperty("cola_company")
    public Object colaCompany;
    @JsonProperty("khl_id")
    public Long khl_id;
    @JsonProperty("team_a")
    public TeamA teamA;
    @JsonProperty("team_b")
    public TeamB teamB;
    @JsonProperty("name")
    public String name;
    @JsonProperty("tickets")
    public Object tickets;
    @JsonProperty("location")
    public String location;
    @JsonProperty("start_at_day")
    public Long startAtDay;
    @JsonProperty("start_at")
    public Long startAt;
    @JsonProperty("event_start_at")
    public Long eventStartAt;
    @JsonProperty("end_at")
    public Long endAt;
    @JsonProperty("not_regular")
    public Boolean notRegular;
    @JsonProperty("score")
    public String score;
    @JsonProperty("scores")
    public Scores scores;
    @JsonProperty("sscore")
    public String sscore;
    @JsonProperty("image")
    public String image;
    @JsonProperty("has_video")
    public Boolean hasVideo;
    @JsonProperty("m3u8_url")
    public Object m3u8Url;
    @JsonProperty("feed_url")
    public Object feedUrl;
}
