package com.logibeat.cloud.boot.mybatis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 该配置的目的在于约定：寻找类路径下的 mybatis.properties 文件来填充 MybatisProperties
 */
@Configuration
@PropertySource(value = "classpath:mybatis.properties", ignoreResourceNotFound = true)
public class MybatisPropertiesAutoConfiguration {
}
