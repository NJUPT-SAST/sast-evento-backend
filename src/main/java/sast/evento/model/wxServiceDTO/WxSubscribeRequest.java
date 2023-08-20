package sast.evento.model.wxServiceDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/28 16:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class WxSubscribeRequest {
    private String template_id;
    private String page;
    private String touser;//openId
    private Map<String, WxDataNode> data;
    private String miniprogram_state = "developer";//formal
    private String lang = "zh_CN";

    @JsonIgnore
    public static Map<String, WxDataNode> getData(Map<String, String> dataMap) {
        return dataMap.entrySet().stream()
                .collect(
                        HashMap::new,
                        (map, entry) -> map.put(entry.getKey(), new WxDataNode(entry.getValue())),
                        Map::putAll
                );
    }

    @Data
    @AllArgsConstructor
    public static class WxDataNode {
        String value;
    }
}
