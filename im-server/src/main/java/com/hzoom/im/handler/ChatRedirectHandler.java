package com.hzoom.im.handler;

import com.hzoom.core.concurrent.callbackTask.FutureTaskScheduler;
import com.hzoom.im.processor.ChatRedirectProcessor;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.LocalSession;
import com.hzoom.im.session.ServerSession;
import com.hzoom.im.session.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
@Component
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ChatRedirectProcessor redirectProcessor;

    @Autowired
    SessionManger sessionManger;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(redirectProcessor.support())) {
            super.channelRead(ctx, msg);
            return;
        }

        FutureTaskScheduler.add(() -> {
            LocalSession session = LocalSession.getSession(ctx);
            if (null != session && session.isLogin()) {
                redirectProcessor.handle(session, pkg);
                return;
            }
            ProtoMsg.MessageRequest messageRequest = pkg.getMessageRequest();
            List<ServerSession> toSessions = sessionManger.getSessionsBy(messageRequest.getTo());
            final boolean[] isSended = {false};
            toSessions.forEach(serverSession -> {
//                if (serverSession instanceof LocalSession) {
//                    serverSession.send(pkg);
//                    isSended[0] =true;
//                    log.info("信息本地转发");
//                }
                serverSession.send(pkg);
                isSended[0] =true;
            });
            if(!isSended[0]) {
                log.error("用户尚未登录，不能接受消息");
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LocalSession session = ctx.channel().attr(LocalSession.SESSION_KEY).get();
        if (null != session && session.isValid()) {
            session.close();
            sessionManger.removeLocalSession(session.getSessionId());
        }
    }
}
