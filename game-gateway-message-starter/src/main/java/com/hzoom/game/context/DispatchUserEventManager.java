package com.hzoom.game.context;

import com.hzoom.game.message.dispatcher.DispatcherMapping;
import com.hzoom.game.message.dispatcher.MessageHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DispatchUserEventManager {
    @Autowired
    private ApplicationContext context;

    private Map<String, DispatcherMapping> userEventMethodCache = new HashMap<>();

    @PostConstruct
    public void init() {
        Map<String, Object> beans = context.getBeansWithAnnotation(MessageHandler.class);
        beans.values().parallelStream().forEach(c -> {
            Method[] methods = c.getClass().getMethods();
            for (Method method : methods) {//遍历每个类中的方法
                UserEvent userEvent = method.getAnnotation(UserEvent.class);
                if (userEvent != null) {//如果这个方法被@UserEvent注解标记了，缓存下所有的数据
                    String key = userEvent.value().getName();
                    DispatcherMapping dispatcherMapping = new DispatcherMapping(c, method);
                    userEventMethodCache.put(key, dispatcherMapping);
                }
            }
        });
    }

    //通过反射调用处理相应事件的方法
    public void callMethod(UserEventContext<?> ctx,Object event, Promise<Object> promise) {
        String key = event.getClass().getName();
        DispatcherMapping dispatcherMapping = this.userEventMethodCache.get(key);
        if (dispatcherMapping != null) {
            try {
                dispatcherMapping.getTargetMethod().invoke(dispatcherMapping.getTargetClass(), ctx,event, promise);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("事件处理调用失败，事件对象:{},处理对象：{}，处理方法：{}", event.getClass().getName(), dispatcherMapping.getTargetClass().getClass().getName(), dispatcherMapping.getTargetMethod().getName());
            }
        } else {
            log.debug("事件：{} 没有找到处理的方法", event.getClass().getName());
        }
    }

}
