package sast.evento.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @Title JsonUtil
 * @Description 统一使用jackson, 提供序列化工具
 * @Author feelMoose
 * @Date 2023/7/19 16:13
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    public static String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> tClass) {
        return objectMapper.readValue(json, tClass);
    }

}
