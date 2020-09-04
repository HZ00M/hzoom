package com.example.core.redisLock;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class RedisDistributedAspectRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter redisDistributedAspectFilter = new AssignableTypeFilter(RedisDistributedAspect.class);
        scanner.addIncludeFilter(redisDistributedAspectFilter);
        String scannerPakage = this.getClass().getPackage().getName();
        scanner.findCandidateComponents(scannerPakage).forEach(beanDefinition -> {
            beanDefinitionRegistry.registerBeanDefinition(beanDefinition.getBeanClassName(),beanDefinition);
        });
    }
}
