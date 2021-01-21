package com.hzoom.message.channel;

public interface GameChannelHandler {

    void exceptionCaught(AbstractGameChannelHandlerContext ctx, Throwable cause) throws Exception;
}
