package com.hzoom.game.rpc;

import com.hzoom.game.message.message.IMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RPCEvent {
    Class<? extends IMessage> value();
}