package com.hzoom.im.handler;

import com.hzoom.im.client.CommandController;
import com.hzoom.im.session.ClientSession;
import com.hzoom.im.exception.InvalidFrameException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    CommandController commandController;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if (cause instanceof InvalidFrameException) {
            log.error(cause.getMessage());
            ClientSession.getSession(ctx).close();
        } else {

            //捕捉异常信息
            log.error(cause.getMessage());
            ctx.close();

            //开始重连
            commandController.setConnectFlag(false);
            commandController.startConnectServer();
        }
    }
}
