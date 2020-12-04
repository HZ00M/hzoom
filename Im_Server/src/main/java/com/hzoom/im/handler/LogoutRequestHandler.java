package com.hzoom.im.handler;

import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class LogoutRequestHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SessionManger sessionManger;
    /**
     * 收到消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();

        if (!type.equals(ProtoMsg.HeadType.LOGOUT_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        sessionManger.closeSession(ctx);
    }
}
