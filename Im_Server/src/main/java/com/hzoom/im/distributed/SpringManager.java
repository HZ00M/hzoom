package com.hzoom.im.distributed;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringManager implements ApplicationContextAware {
    /**
     * Spring应用上下文环境
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringManager.applicationContext = applicationContext;
    }


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws org.springframework.beans.BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) applicationContext.getBean(clz);
        return result;
    }


    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }


    /**
     * ioc容器中是否包含一个与所给名称匹配的bean定义
     *
     * @param name
     * @return 存在返回true否则返回false
     */
    public static boolean containBean(String name) {
        return applicationContext.containsBean(name);
    }
}
