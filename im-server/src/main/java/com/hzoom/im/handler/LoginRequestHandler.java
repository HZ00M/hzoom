package com.hzoom.im.handler;

import com.hzoom.core.concurrent.callbackTask.CallbackTask;
import com.hzoom.core.concurrent.callbackTask.CallbackTaskScheduler;
import com.hzoom.im.processor.LoginServerProcessor;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.LocalSession;
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
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private SessionManger sessionManger;
    @Autowired
    private LoginServerProcessor loginServerProcessor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();
        if (!type.equals(loginServerProcessor.support())) {
            super.channelRead(ctx, msg);
            return;
        }
        LocalSession session = new LocalSession(ctx.channel());

        //异步处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {

            @Override
            public Boolean execute() throws Exception {
                return loginServerProcessor.handle(session, pkg);
            }

            @Override
            public void onBack(Boolean isSuccess) {
                if (isSuccess) {
                    ctx.pipeline().remove(LoginRequestHandler.class);
                    log.info("登录成功: {}", session.getUser());
                } else {
                    sessionManger.closeSession(ctx);
                    log.info("登录失败: {}", session.getUser());
                }
            }

            @Override
            public void onException(Throwable t) {
                sessionManger.closeSession(ctx);
                log.info("登录失败: {}", session.getUser());
            }
        });
    }
}
