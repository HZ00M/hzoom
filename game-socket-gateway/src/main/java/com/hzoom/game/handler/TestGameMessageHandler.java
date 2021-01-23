package com.hzoom.game.handler;

import com.hzoom.game.message.GameMessageManager;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.message.request.FirstMsgRequest;
import com.hzoom.game.message.request.SecondMsgRequest;
import com.hzoom.game.message.response.FirstMsgResponse;
import com.hzoom.game.message.response.SecondMsgResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
@Slf4j
public class TestGameMessageHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private GameMessageManager gameMessageManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage gameMessagePackage = (MessagePackage) msg;
        int messageId = gameMessagePackage.getHeader().getMessageId();
        if (messageId == 10001) {
            FirstMsgRequest request = new FirstMsgRequest();
            request.read(gameMessagePackage.body());
            log.debug("接收到客户端消息：{}", request.getValue());
            FirstMsgResponse response = new FirstMsgResponse();
            response.setServerTime(System.currentTimeMillis());
            MessagePackage returnPackage = new MessagePackage();
            returnPackage.setHeader(response.getHeader());
            returnPackage.setBody(response.body());
            ctx.writeAndFlush(returnPackage);
        } else if (messageId == 10002) {
            SecondMsgRequest request = (SecondMsgRequest) gameMessageManager.getRequestInstanceByMessageId(messageId);
            request.read(gameMessagePackage.body());
            log.debug("收到request3:{}", request);
            SecondMsgResponse response = new SecondMsgResponse();
            response.getBodyObj().setResult1(System.currentTimeMillis());
            response.getBodyObj().setResult2("服务器回复");
            MessagePackage returnPackage = new MessagePackage();
            returnPackage.setHeader(response.getHeader());
            returnPackage.setBody(response.body());
            ctx.writeAndFlush(returnPackage);
        } 
    }
}