package com.hzoom.message.config;

import com.hzoom.message.annotation.StartChannelServer;
import com.hzoom.message.enums.ChannelType;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelImportSelector implements DeferredImportSelector, BeanClassLoaderAware, EnvironmentAware {
    private ClassLoader classLoader;
    private Environment environment;
    private Class annotationClass = StartChannelServer.class;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //是否生效，默认为true
        if (!isEnable()) {
            return new String[0];
        }
        //获取注解中的属性
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(this.annotationClass.getName(), true));
        Assert.notNull(attributes, "can not be null ..");
        //从spring.factories中获取所有通过EnableFilter注解引入的自动配置类，并进行去重操作;
        List<String> factories = SpringFactoriesLoader.loadFactoryNames(this.annotationClass, this.classLoader)
                .stream().distinct().collect(Collectors.toList());
        if (factories.isEmpty()) {
            throw new IllegalStateException("factories is empty ..");
        }
        ChannelType channelType = (ChannelType)attributes.get("value");
        String[] typeClazz = new String[1];
        typeClazz[0] = channelType.getChannelType();
        return typeClazz;
    }

    private boolean isEnable() {
        return environment.getProperty("game.server.config.enabled", Boolean.class, Boolean.TRUE);
    }
}
