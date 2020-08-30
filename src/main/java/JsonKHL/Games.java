package JsonKHL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "event"
})
public class Games {

    @JsonProperty("event")
    public Event event;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    Games(){}

    public static final Comparator<Games> COMPARE_BY_TIME = new Comparator<Games>() {
        @Override
        public int compare(Games games, Games t1) {
            return Long.compare(games.event.startAt, t1.event.startAt);
        }
    };
}
