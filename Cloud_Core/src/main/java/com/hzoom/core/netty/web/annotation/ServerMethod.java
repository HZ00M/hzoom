package com.hzoom.core.netty.web.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServerMethod {
    Type value();

    enum Type {
        BeforeHandshake,OnClose,OnError, OnIdleEvent,OnMessage,OnOpen,OnBinary
    }
}
