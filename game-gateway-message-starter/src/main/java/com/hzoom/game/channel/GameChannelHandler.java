package com.hzoom.game.channel;

public interface GameChannelHandler {

    void exceptionCaught(AbstractGameChannelHandlerContext ctx, Throwable cause) throws Exception;
}
