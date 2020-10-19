package com.example.core.netty.web.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.InputStream;

public interface Handler {
    void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) ;
}
