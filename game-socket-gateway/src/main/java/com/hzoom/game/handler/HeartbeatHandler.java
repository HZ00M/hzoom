package com.hzoom.game.handler;

import com.hzoom.game.common.GatewayMessageTypeEnum;
import com.hzoom.game.message.common.MessagePackage;
import com.hzoom.game.message.response.HeartbeatMsgResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private int heartbeatCount = 0;// 心跳计数器，如果一直接收到的是心跳消息，达到一定数量之后，说明客户端一直没有用户操作了，服务器就主动断开连接。
    private int maxHeartbeatCount = 66;// 最大心跳数

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                ctx.close();
                log.info("连接读取空闲，断开连接，channelId:{}", ctx.channel().id().asShortText());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage messagePackage = (MessagePackage) msg;// 拦截心跳请求，并处理
        if (messagePackage.getHeader().getMessageId() == GatewayMessageTypeEnum.Heartbeat.getMessageId()) {
            HeartbeatMsgResponse response = new HeartbeatMsgResponse();
            response.getBodyObj().setServerTime(System.currentTimeMillis());
            MessagePackage returnPackage = new MessagePackage(response);
            ctx.writeAndFlush(returnPackage);
            this.heartbeatCount++;
            if (heartbeatCount > maxHeartbeatCount) {
                ctx.close();
            }
        } else {
            this.heartbeatCount = 0;
            ctx.fireChannelRead(msg);
        }
    }
}
