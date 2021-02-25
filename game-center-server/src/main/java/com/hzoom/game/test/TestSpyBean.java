package com.hzoom.game.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestSpyBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware,InitializingBean,BeanPostProcessor, DisposableBean {
    @Autowired
    private TestMockBean testMockBean;
    private BeanFactory beanFactory;
    private ApplicationContext applicationContext;

    public int getValue() {
        return 3;
    }

    public int getMockBeanLevel() {
        return testMockBean.getValue();
    }

    private String getName(int a) {
        return "a";
    }

    public int getValue(int type) {
        int value = 0;
        switch (type) {
            case 1:
                value = 100;
                break;
            case 2:
                value = 200;
                break;
            case 3:
                value = 300;
                break;
            default:
                value = 500;
                break;
        }
        return value;
    }

    public void saveData(String value) {
        if (value != null && !value.isEmpty()) {
            testMockBean.saveToRedis(value);
        }
    }

    public static int queryValue() {
        return 3;
    }

    public void calculate(int a) {
        int value = (a + 3) * 2;
        this.getName(value);
    }


    @Override
    public void destroy() throws Exception {
        log.info("7 destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("4 afterPropertiesSet  这里可以调用自定义初始化方法");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info("5 bean初始化前调用postProcessBeforeInitialization {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("6 bean初始化后调用postProcessAfterInitialization {}  bean可以使用了", beanName);
        return bean;
    }

    @Override
    public void setBeanName(String beanName) {
        log.info("1 BeanNameAware {}", beanName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

        DefaultListableBeanFactory defaultListableBeanFactory =(DefaultListableBeanFactory) beanFactory;
        defaultListableBeanFactory.destroySingleton("testSpyBean");
        log.info("2 setBeanFactory");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        log.info("3 setApplicationContext");
    }
}