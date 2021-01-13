package com.hzoom.game.message.dispatcher;

import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class DispatchMessageService {
    @Autowired
    private ApplicationContext applicationContext;
    private Map<String, DispatcherMapping> dispatcherMappingMap = new HashMap<>();

    /**
     * 服务启动的时候调用此方法，扫描获取此服务要处理的game message类。
     *
     * @param applicationContext
     * @param serviceId
     * @param packagePath
     */
    public static void scanGameMessages(ApplicationContext applicationContext, int serviceId, String packagePath) {
        DispatchMessageService dispatchMessageService = applicationContext.getBean(DispatchMessageService.class);
        dispatchMessageService.scanGameMessages(serviceId, packagePath);
    }

    public void scanGameMessages(int serviceId, String packagePath) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> allHandlerClass = reflections.getTypesAnnotatedWith(MessageHandler.class);
        if (allHandlerClass != null){
            allHandlerClass.forEach(c->{
                Object targetBean = applicationContext.getBean(c);
                Method[] methods = c.getMethods();
                for (Method targetMethod : methods) {
                    MessageMapping messageMapping = targetMethod.getAnnotation(MessageMapping.class);
                    if (messageMapping!=null){
                        Class<? extends IMessage> messageClass = messageMapping.value();
                        MessageMetadata messageMetadata = messageClass.getAnnotation(MessageMetadata.class);
                        if (serviceId == 0 || serviceId == messageMetadata.serviceId()){// 每个服务只加载自己可以处理的消息类型,如果为0则加载所有的类型
                            DispatcherMapping dispatcherMapping = new DispatcherMapping(targetBean,targetMethod);
                            dispatcherMappingMap.put(messageClass.getName(),dispatcherMapping);
                        }
                    }
                }
            });
        }
    }

    public void callMethod(IMessage message, IChannelContext ctx) {// 当收到网络消息之后，调用此方法。
        String key = message.getClass().getName();
        DispatcherMapping dispatcherMapping = dispatcherMappingMap.get(key);
        if (dispatcherMapping!=null){
            Object targetClass = dispatcherMapping.getTargetClass();
            try {
                dispatcherMapping.getTargetMethod().invoke(targetClass,message,ctx);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("调用方法异常，方法所在类：{}，方法名：{}", dispatcherMapping.getTargetClass().getClass().getName(), dispatcherMapping.getTargetMethod().getName(), e);
            }
        }else {
            log.warn("消息未找到处理的方法，消息名：{}", key);
        }
    }
}
