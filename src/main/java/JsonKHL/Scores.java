package JsonKHL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "first_period",
    "second_period",
    "third_period",
    "overtime",
    "bullitt"
})
public class Scores {

    @JsonProperty("first_period")
    public String firstPeriod;
    @JsonProperty("second_period")
    public String secondPeriod;
    @JsonProperty("third_period")
    public String thirdPeriod;
    @JsonProperty("overtime")
    public String overtime;
    @JsonProperty("bullitt")
    public Object bullitt;

}
