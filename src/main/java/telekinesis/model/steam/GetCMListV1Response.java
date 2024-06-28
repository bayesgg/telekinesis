package telekinesis.model.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetCMListV1Response {
    List<String> serverlist;
    @JsonProperty("serverlist_websockets")
    List<String> serverlistWebsockets;
    int result;
    String message;
}
