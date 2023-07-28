package sast.evento.model.wxServiceDTO;

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
    private Map<String, Map<String, String>> data;
    private String miniprogram_state = "developer";//formal
    private String lang = "zh_CN";

    public static Map<String, Map<String, String>> getData(Map<String, String> dataMap) {
        Map<String, Map<String, String>> map = new HashMap<>();
        dataMap.forEach((key, value) -> {
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("value", value);
            map.put(key, stringMap);
        });
        return map;
    }
}
