package com.hzoom.game.handler.common;

import com.hzoom.game.client.GameClientChannelContext;
import com.hzoom.game.message.DispatchMessageManager;
import com.hzoom.game.message.common.IMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  接收服务器响应的消息，并将消息分发到业务处理方法中。
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class DispatchGameMessageHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private DispatchMessageManager dispatchMessageManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMessage message = (IMessage)msg;
        GameClientChannelContext context = new GameClientChannelContext(ctx.channel(),message);
        dispatchMessageManager.callMethod(context,message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("{} 连接断开，channelId {}",this.getClass().getSimpleName(),ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} 服务异常 {}",this.getClass().getSimpleName(),cause);
    }
}
