package com.hzoom.im.handler;

import com.hzoom.im.exception.InvalidFrameException;
import com.hzoom.im.session.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerExceptionHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SessionManger sessionManger;
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvalidFrameException) {
            log.error(cause.getMessage());
        } else {
            //捕捉异常信息
            cause.printStackTrace();
            log.error(cause.getMessage());
        }
        sessionManger.closeSession(ctx);
        ctx.close();
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionManger.closeSession(ctx);
    }
}
