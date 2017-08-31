package com.logibeat.cloud.boot.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.lang.reflect.Field;

@Configuration
public class JedisTemplateInitializer {

    @Bean
    public JedisTemplate jedisTemplate(JedisConnectionFactory redisConnectionFactory) throws Exception {
        Field field = redisConnectionFactory.getClass().getDeclaredField("pool");
        field.setAccessible(true);
        Pool<Jedis> pool = (Pool<Jedis>) field.get(redisConnectionFactory);
        return new JedisTemplate(pool);
    }
}
