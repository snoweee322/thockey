
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
    "abstractGameState",
    "codedGameState",
    "detailedState",
    "statusCode",
    "startTimeTBD"
})
public class Status {

    @JsonProperty("abstractGameState")
    public String abstractGameState;
    @JsonProperty("codedGameState")
    public String codedGameState;
    @JsonProperty("detailedState")
    public String detailedState;
    @JsonProperty("statusCode")
    public String statusCode;
    @JsonProperty("startTimeTBD")
    public Boolean startTimeTBD;
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
