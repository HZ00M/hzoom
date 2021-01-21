package com.hzoom.message.channel;

import com.hzoom.game.message.message.IMessage;
import io.netty.util.concurrent.Promise;

public interface GameChannelOutboundHandler extends GameChannelHandler {
    
    void writeAndFlush(AbstractGameChannelHandlerContext ctx, IMessage msg, GameChannelPromise promise) throws Exception;

    void writeRPCMessage(AbstractGameChannelHandlerContext ctx, IMessage gameMessage, Promise<IMessage> callback);

    void close(AbstractGameChannelHandlerContext ctx, GameChannelPromise promise);
   
}
