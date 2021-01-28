package com.hzoom.game.message;

import com.hzoom.game.message.common.AbstractMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class GameMessageManager {
    private Map<String, Class<? extends IMessage>> msgClassMap = new HashMap<>();

    public IMessage getRequestInstanceByMessageId(int messageId){
        return getMessageInstance(messageId, IMessage.MessageType.REQUEST);
    }

    public IMessage getResponseInstanceByMessageId(int messageId){
        return getMessageInstance(messageId, IMessage.MessageType.RESPONSE);
    }

    public IMessage getMessageInstance(int messageId, IMessage.MessageType messageType) {
        String key = getMetadataKey(messageId, messageType);
        Class<? extends IMessage> clazz = msgClassMap.get(key);
        if (clazz ==null){
            throw new IllegalArgumentException("找不到messageId:" + key + "对应的响应数据对象Class");
        }
        IMessage message = null;
        try {
            message = clazz.newInstance();
        } catch (IllegalAccessException |InstantiationException e) {
            String msg = "实例化响应参数出现," + "messageId:" + key + ", class:" + clazz.getName();
            log.error(msg, e);
            throw new IllegalArgumentException(msg);
        }
        return message;
    }

    @PostConstruct
    public void init() {
        Reflections reflections = new Reflections("com.hzoom");
        Set<Class<? extends AbstractMessage>> classSet = reflections.getSubTypesOf(AbstractMessage.class);
        classSet.forEach(c -> {
            MessageMetadata metadata = c.getAnnotation(MessageMetadata.class);
            if (metadata != null) {
                checkMessageMetadata(metadata, c);
                int messageId = metadata.messageId();
                IMessage.MessageType messageType = metadata.messageType();
                String key = getMetadataKey(messageId, messageType);
                msgClassMap.put(key, c);
            }
        });
    }

    private String getMetadataKey(int messageId, IMessage.MessageType messageType) {
        return messageId + "_" + messageType;
    }

    private void checkMessageMetadata(MessageMetadata metadata, Class<? extends AbstractMessage> c) {
        int messageId = metadata.messageId();
        if (messageId == 0) {
            throw new IllegalArgumentException("messageId未设置:" + c.getName());
        }
        int serviceId = metadata.serviceId();
        if (serviceId == 0) {
            throw new IllegalArgumentException("serviceId未设置：" + c.getName());
        }
        IMessage.MessageType messageType = metadata.messageType();
        if (messageType == null) {
            throw new IllegalArgumentException("messageType未设置:" + c.getName());
        }
    }
}
