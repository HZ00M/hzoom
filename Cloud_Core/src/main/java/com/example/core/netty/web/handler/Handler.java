package com.example.core.netty.web.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;


public interface Handler {

    void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) ;

}
