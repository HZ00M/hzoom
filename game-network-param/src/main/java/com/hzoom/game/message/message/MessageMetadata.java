package com.hzoom.game.message.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMetadata {
    int messageId();    //消息请求id
    int serviceId();    //服务id
    IMessage.MessageType messageType();  //消息类型
}
