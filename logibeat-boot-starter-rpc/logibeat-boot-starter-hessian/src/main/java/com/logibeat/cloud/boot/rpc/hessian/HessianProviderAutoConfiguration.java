package com.logibeat.cloud.boot.rpc.hessian;

import com.logibeat.cloud.boot.rpc.common.annotation.RpcProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import java.util.Arrays;

@Slf4j
@Configuration
public class HessianProviderAutoConfiguration implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(RpcProvider.class);
        for (String beanName : beanNames) {
            String className = beanFactory.getBeanDefinition(beanName).getBeanClassName(); // 全名
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeanInitializationException(e.getMessage(), e);
            }
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 0) {
                throw new BeanInitializationException(className + " 没有实现任何接口，无法导出为 hessian 服务。");
            }
            Class<?> serviceInterface;
            RpcProvider rpcProvider = clazz.getDeclaredAnnotation(RpcProvider.class);
            if (rpcProvider.interfaceClass() != void.class) {
                if (Arrays.asList(interfaces).contains(rpcProvider.interfaceClass())) {
                    serviceInterface = rpcProvider.interfaceClass();
                } else {
                    throw new BeanInitializationException(
                            className + " 被注解 @" + RpcProvider.class.getSimpleName() + " 标注，且其 interfaceClass 为 " + rpcProvider.interfaceClass().getName()
                                    + "，但并没有实现该接口。"
                    );
                }
            } else {
                serviceInterface = interfaces[0];
                if (interfaces.length > 1) {
                    log.warn(className + " 实现了多个接口，将使用" + serviceInterface.getName() + "作为接口导出为 hessian 服务。");
                }
            }

            String hessianServiceBeanName = "/" + ("".equals(rpcProvider.interfaceName()) ? lowerFirstLetter(serviceInterface.getSimpleName()) : rpcProvider.interfaceName());
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HessianServiceExporter.class);
            builder.addPropertyReference("service", beanName);
            builder.addPropertyValue("serviceInterface", serviceInterface.getName());
            ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(hessianServiceBeanName, builder.getBeanDefinition());
            log.info("{} 已导出为 hessian 服务，uri = {}", className, hessianServiceBeanName);
        }
    }

    private String lowerFirstLetter(String src) {
        if (src.length() == 1) {
            return src.toLowerCase();
        }
        return src.substring(0, 1).toLowerCase() + src.substring(1);
    }
}
