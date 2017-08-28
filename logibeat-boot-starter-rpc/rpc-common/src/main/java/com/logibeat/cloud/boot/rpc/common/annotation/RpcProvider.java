package com.logibeat.cloud.boot.rpc.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcProvider {
    /**
     * 代理的接口类型
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 代理的接口名称
     */
    String interfaceName() default "";

    /**
     * 服务所属组名
     */
    String group() default "";

    /**
     * 服务的版本号
     */
    String version() default "";

    /**
     * 格式为“protocol id:port”
     */
    String export() default "";
}