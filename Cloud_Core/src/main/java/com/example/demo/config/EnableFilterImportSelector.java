package com.example.demo.config;

import com.example.demo.annotation.EnableFilter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class EnableFilterImportSelector implements ImportSelector, BeanClassLoaderAware, EnvironmentAware {

    private ClassLoader classLoader;
    private Environment environment;
    private Class annotationClass = EnableFilter.class;

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
        ArrayList<String> factories = new ArrayList<>(new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(this.annotationClass, this.classLoader)));
        if (factories.isEmpty()) {
            throw new IllegalStateException("factories is empty ..");
        }
        factories.stream().forEach(filter -> System.out.println("starting  " + filter));
        return factories.toArray(new String[factories.size()]);
    }

    private boolean isEnable() {
        return environment.getProperty("cloud.filter.enable", Boolean.class, Boolean.TRUE);
    }
}
