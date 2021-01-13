package com.hzoom.game.handler.common;

import com.hzoom.game.message.GameMessageService;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessagePackage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class ResponseHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private GameMessageService gameMessageService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage messagePackage = (MessagePackage) msg;
        int messageId = messagePackage.getHeader().getMessageId();
        IMessage response = gameMessageService.getResponseInstanceByMessageId(messageId);
        response.setHeader(messagePackage.getHeader());
        response.read(messagePackage.body());
        ctx.fireChannelRead(response);
    }
}
