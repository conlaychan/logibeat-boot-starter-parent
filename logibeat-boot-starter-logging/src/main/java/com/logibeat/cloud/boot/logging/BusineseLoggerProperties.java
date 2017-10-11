package com.logibeat.cloud.boot.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;

@Data
@ConfigurationProperties(prefix = "logging.businese")
public class BusineseLoggerProperties {

    /**
     * 是否启用业务日志与系统日志的分流
     */
    private boolean enabled = false;

    private String file;
    private LogLevel level = LogLevel.INFO;
    private String fileLogPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p --- [%t] %class[%line] : %m%n";
    private String consoleLogPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p --- [%t]%replace(%caller{1}){'\\t|Caller.{1}0|\\r\\n', ''} : %m%n";
}
