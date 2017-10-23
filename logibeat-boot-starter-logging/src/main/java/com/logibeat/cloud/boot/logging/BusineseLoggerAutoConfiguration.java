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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.nio.charset.Charset;

@Slf4j
@Configuration
@EnableConfigurationProperties(BusineseLoggerProperties.class)
@ConditionalOnMissingBean(value = Logger.class, name = BusineseLoggerAutoConfiguration.BUSINESE_LOGGER_NAME)
public class BusineseLoggerAutoConfiguration {

    public static final String BUSINESE_LOGGER_NAME = "busineseLogger";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusineseLoggerProperties properties;

    /**
     * 当明确禁用业务日志分流时也仍然要实例化一个busineseLogger，以免自动装配此bean的代码报错
     */
    @ConditionalOnProperty(prefix = "logging.businese", name = "enabled", havingValue = "false")
    static class DefaultBusineseLogger {
        @Bean
        public Logger busineseLogger() {
            log.warn("业务日志与系统日志的分流已禁用，现在 {} 将继承 root logger 的配置。", BUSINESE_LOGGER_NAME);
            return LoggerFactory.getLogger(BUSINESE_LOGGER_NAME);
        }
    }

    @ConditionalOnProperty(prefix = "logging.businese", name = "enabled", havingValue = "true", matchIfMissing = true)
    class ConfigurableBusineseLogger {
        @Bean
        public Logger busineseLogger() {
            log.info("业务日志与系统日志的分流已启用");
            Logger busineseLogger = LoggerFactory.getLogger(BUSINESE_LOGGER_NAME);
            if (ClassUtils.isPresent("ch.qos.logback.classic.Logger", applicationContext.getClassLoader()) && busineseLogger instanceof ch.qos.logback.classic.Logger) {
                ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) busineseLogger;
                logger.setAdditive(false);
                LoggingSystem.get(applicationContext.getClassLoader()).setLogLevel(BUSINESE_LOGGER_NAME, properties.getLevel());
                consoleAppender(logger);
                if (properties.getFile() != null) {
                    fileAppender(logger, properties.getFile());
                } else {
                    log.warn("您已启用了业务日志与系统日志的分流，但您没有配置 logging.businese.file，业务日志将不会输出到任何文件。");
                }
            } else {
                log.warn("业务日志与系统日志的分流，目前仅支持 logback，但您的当前日志实现实际为 {}，现在 {} 将继承 root logger 的配置。", busineseLogger.getClass().getName(), BUSINESE_LOGGER_NAME);
            }
            return busineseLogger;
        }
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
