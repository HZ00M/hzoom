package com.hzoom.core.netty.web.annotation;

import com.hzoom.core.netty.web.filter.*;
import io.netty.channel.ChannelHandler;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerEndpoint {
    Class<? extends Filter>[] beforeHandShakeFilters() default {BadRequestFilter.class, OnlyGetFilter.class, CheckHostFilter.class, ResourceFilter.class,PathFilter.class,UpGradeFilter.class};

    Class<? extends ChannelHandler>[] beforeWebSocketHandlers() default {};

    @AliasFor("path")
    String value() default "/";

    @AliasFor("value")
    String path() default "/";

    String host() default "0.0.0.0";

    String port() default "80";

    String bossLoopGroupThreads() default "0";

    String workerLoopGroupThreads() default "0";

    //------------------------- handler -------------------------

    String userIdleStateHandler() default "true";

    String useCompressionHandler() default "false";

    String useWebSocketFrameAggregator() default "true";

    //------------------------- option -------------------------

    String optionConnectTimeoutMillis() default "30000";

    String optionSoBacklog() default "128";

    //------------------------- childOption -------------------------

    String childOptionWriteSpinCount() default "16";

    String childOptionWriteBufferHighWaterMark() default "65536";

    String childOptionWriteBufferLowWaterMark() default "32768";

    String childOptionSoRcvbuf() default "-1";

    String childOptionSoSndbuf() default "-1";

    String childOptionTcpNodelay() default "true";

    String childOptionSoKeepalive() default "false";

    String childOptionSoLinger() default "-1";

    String childOptionAllowHalfClosure() default "false";

    //------------------------- idleEvent -------------------------

    String readerIdleTimeSeconds() default "0";

    String writerIdleTimeSeconds() default "0";

    String allIdleTimeSeconds() default "0";

    //------------------------- handshake -------------------------

    String maxFramePayloadLength() default "65536";
}
