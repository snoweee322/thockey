package JsonKHL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "image",
    "name",
    "location"
})
public class TeamB {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("khl_id")
    public Long khl_id;
    @JsonProperty("image")
    public String image;
    @JsonProperty("name")
    public String name;
    @JsonProperty("location")
    public String location;
}
