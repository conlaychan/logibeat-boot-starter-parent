package com.logibeat.cloud.boot.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Slf4j
@Configuration
@EnableConfigurationProperties(BusineseLoggerProperties.class)
public class LogibeatLoggingAutoConfiguration {

    private static final String BUSINESE_LOGGER_NAME = "busineseLogger";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusineseLoggerProperties properties;

    @Bean
    @ConditionalOnProperty(prefix = "logging.businese", name = "enabled", havingValue = "true")
    @ConditionalOnClass(ch.qos.logback.classic.Logger.class)
    public Logger busineseLogger() {
        Logger busineseLogger = LoggerFactory.getLogger(BUSINESE_LOGGER_NAME);
        if (busineseLogger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) busineseLogger;
            logger.setAdditive(false);
            LoggingSystem.get(applicationContext.getClassLoader()).setLogLevel(BUSINESE_LOGGER_NAME, properties.getLevel());
            if (properties.getFile() != null) {
                fileAppender(logger, properties.getFile());
            }
            consoleAppender(logger);
        } else {
            log.warn("业务日志与系统日志的分流，目前仅支持 logback，但您的当前日志实现实际为 {}。", busineseLogger.getClass());
        }
        return busineseLogger;
    }

    private void fileAppender(ch.qos.logback.classic.Logger logger, String logFile) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(properties.getFileLogPattern());
        appender.setEncoder(encoder);
        appender.setFile(logFile);
        encoder.setContext(logger.getLoggerContext());

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setFileNamePattern(logFile + ".%i");
        appender.setRollingPolicy(rollingPolicy);
        rollingPolicy.setParent(appender);
        rollingPolicy.setContext(logger.getLoggerContext());

        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
        triggeringPolicy.setMaxFileSize(FileSize.valueOf("10MB"));
        appender.setTriggeringPolicy(triggeringPolicy);
        triggeringPolicy.setContext(logger.getLoggerContext());

        appender.setContext(logger.getLoggerContext());
        rollingPolicy.start();
        triggeringPolicy.start();
        encoder.start();
        appender.start();
        logger.addAppender(appender);
    }

    private void consoleAppender(ch.qos.logback.classic.Logger logger) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(properties.getConsoleLogPattern());
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(logger.getLoggerContext());
        encoder.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setEncoder(encoder);
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);
    }
}
