package com.logibeat.cloud.boot.redis;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

public class JedisTemplate {

    private final Pool<Jedis> jedisPool;

    public JedisTemplate(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> T execute(JedisAction<T> jedisAction) {
        return execute(jedisAction, 0);
    }

    public <T> T execute(JedisAction<T> jedisAction, int dbIndex) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dbIndex);
            return jedisAction.action(jedis);
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public void execute(JedisActionNoResult jedisAction) {
        execute(jedisAction, 0);
    }

    public void execute(JedisActionNoResult jedisAction, int dbIndex) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dbIndex);
            jedisAction.action(jedis);
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public interface JedisAction<T> {
        T action(Jedis jedis);
    }

    public interface JedisActionNoResult {
        void action(Jedis jedis);
    }
}
