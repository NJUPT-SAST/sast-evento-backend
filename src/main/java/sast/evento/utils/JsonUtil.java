package sast.evento.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/15 16:04
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper;
    static {
       objectMapper = new ObjectMapper();
    }
    @SneakyThrows
    public static String toJson(Object object){
        return objectMapper.writeValueAsString(object);
    }
    @SneakyThrows
    public static <T> T fromJson(String json,Class<T> tClass){
        return objectMapper.readValue(json,tClass);
    }

}
