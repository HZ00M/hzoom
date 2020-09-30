package com.example.core.netty.web.annotation;

import com.example.core.netty.web.enums.ListenerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServerListener {
    ListenerType value();
}
