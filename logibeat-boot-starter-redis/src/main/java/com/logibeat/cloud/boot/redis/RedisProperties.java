package com.logibeat.cloud.boot.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    //properties with default value

    private int port = 6379;
    private int maxTotal = 5;
    private int maxIdle = 0;
    private int maxWaitMillis = 10000;
    private boolean testOnBorrow = true;
    private boolean cluster = false;

    //properties without default value

    private String host;
    // 监控服务器地址(需外部指定)
    private String sentinelHosts;
    // 监控Master名称(需外部指定)
    private String sentinelMasterName;



}
