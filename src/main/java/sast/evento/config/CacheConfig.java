package sast.evento.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import sast.evento.utils.RedisUtil;

import java.time.Duration;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/15 13:03
 */
@EnableCaching
@Configuration
public class CacheConfig {
    @Value("${cache.duration}")
    private int duration;
    @Resource
    ObjectMapper objectMapper;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory){
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(duration))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(JsonRedisSerializer))
                .prefixCacheNameWith(RedisUtil.prefix + "cache:");
        return RedisCacheManager.builder(factory)
                .cacheDefaults(configuration)
                .build();
    }



}
