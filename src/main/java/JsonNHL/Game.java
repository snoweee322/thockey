
package JsonNHL;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gamePk",
    "link",
    "gameType",
    "season",
    "gameDate",
    "status",
    "teams",
    "venue",
    "content"
})
public class Game {

    @JsonProperty("gamePk")
    public Long gamePk;
    @JsonProperty("link")
    public String link;
    @JsonProperty("gameType")
    public String gameType;
    @JsonProperty("season")
    public String season;
    @JsonProperty("gameDate")
    public String gameDate;
    @JsonProperty("status")
    public Status status;
    @JsonProperty("teams")
    public Teams teams;
    @JsonProperty("venue")
    public Venue venue;
    @JsonProperty("content")
    public Content content;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
