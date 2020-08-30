
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
    "copyright",
    "totalItems",
    "totalEvents",
    "totalGames",
    "totalMatches",
    "wait",
    "dates"
})
public class NHLStats {

    @JsonProperty("copyright")
    public String copyright;
    @JsonProperty("totalItems")
    public Long totalItems;
    @JsonProperty("totalEvents")
    public Long totalEvents;
    @JsonProperty("totalGames")
    public Long totalGames;
    @JsonProperty("totalMatches")
    public Long totalMatches;
    @JsonProperty("wait")
    public Long wait;
    @JsonProperty("dates")
    public List<Date> dates = null;
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
