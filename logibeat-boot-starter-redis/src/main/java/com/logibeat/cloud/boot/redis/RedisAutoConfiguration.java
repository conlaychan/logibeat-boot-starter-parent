package com.logibeat.cloud.boot.redis;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.Set;

@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {

    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getMaxTotal());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMaxWaitMillis(properties.getMaxWaitMillis());
        config.setTestOnBorrow(properties.isTestOnBorrow());
        return config;
    }

    @Bean
    public Pool<Jedis> jedisPool(JedisPoolConfig config) {
        if (properties.isCluster()) {
            String sentinelProps = properties.getSentinelHosts();
            Iterable<String> parts = Splitter.on(',').trimResults().omitEmptyStrings().split(sentinelProps);

            final Set<String> sentinelHosts = Sets.newHashSet(parts);
            String masterName = properties.getSentinelMasterName();
            return new JedisSentinelPool(masterName, sentinelHosts, jedisPoolConfig());
        }
        return new JedisPool(config, properties.getHost(), properties.getPort());
    }

    @Bean
    @ConditionalOnMissingBean(JedisTemplate.class)
    public JedisTemplate jedisTemplate(Pool<Jedis> pool) {
        return new JedisTemplate(pool);
    }
}
