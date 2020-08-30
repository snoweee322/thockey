
package JsonNHL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "totalItems",
    "totalEvents",
    "totalGames",
    "totalMatches",
    "games",
    "events",
    "matches"
})
public class Date {

    @JsonProperty("date")
    public String date;
    @JsonProperty("totalItems")
    public Long totalItems;
    @JsonProperty("totalEvents")
    public Long totalEvents;
    @JsonProperty("totalGames")
    public Long totalGames;
    @JsonProperty("totalMatches")
    public Long totalMatches;
    @JsonProperty("games")
    public List<Game> games = null;
    @JsonProperty("events")
    public List<Object> events = null;
    @JsonProperty("matches")
    public List<Object> matches = null;
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
