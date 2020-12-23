package com.hzoom.im.handler;

import com.hzoom.core.concurrent.callbackTask.FutureTaskScheduler;
import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.SessionManger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Scope("prototype")
public class HeartBeatServerHandler extends IdleStateHandler {
    private static final int READ_IDLE_GAP = 150;
    @Autowired
    private SessionManger sessionManger;
    public HeartBeatServerHandler(){
        super(READ_IDLE_GAP,0,0, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        //判断消息类型
        ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            //异步处理,将心跳包，直接回复给客户端
            FutureTaskScheduler.add(() -> {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(msg);
                }
            });

        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info(READ_IDLE_GAP + "秒内未读到数据，关闭连接",ctx.channel().attr(ServerConstants.NODE_KEY).get());
        sessionManger.closeSession(ctx);
    }
}
