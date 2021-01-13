package com.hzoom.game.message.dispatcher;

import com.hzoom.game.message.message.IMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMapping {

    Class<? extends IMessage> value();
}
