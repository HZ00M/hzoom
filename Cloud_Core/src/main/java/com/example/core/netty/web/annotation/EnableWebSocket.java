package com.example.core.netty.web.annotation;

import com.example.core.netty.web.autoconfigure.NettyWebSocketSelector;
import com.example.core.netty.web.handler.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(NettyWebSocketSelector.class)
public @interface EnableWebSocket {

}
