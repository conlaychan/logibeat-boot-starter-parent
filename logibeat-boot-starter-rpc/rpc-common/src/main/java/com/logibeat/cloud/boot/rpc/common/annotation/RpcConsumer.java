package com.logibeat.cloud.boot.rpc.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcConsumer {
    /**
     * 服务所属组名
     */
    String group() default "";

    /**
     * 服务的版本号
     */
    String version() default "";

    /**
     * 服务调用超时时间
     */
    String timeout() default "";

    /**
     * 是否检查服务提供者是否存活
     */
    String check() default "";

    /**
     * 重试次数
     */
    int retries() default 0;

    /**
     * 直连url
     */
    String directUrl() default "";

    /**
     * 协议
     */
    String protocol() default "";

    /**
     * 是否指定注册中心，若指定，则从指定注册中心获取服务
     */
    boolean specifiedRegistry() default false;

    /**
     * 唯一标识每个注册中心的key
     *
     * @see
     */
    String registryKey() default "";
}