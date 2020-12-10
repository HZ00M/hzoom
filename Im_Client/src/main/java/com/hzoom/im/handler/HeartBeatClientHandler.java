package com.hzoom.im.handler;

import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.builder.HeartBeatMsgBuilder;
import com.hzoom.im.clientSession.ClientSession;
import com.hzoom.im.proto.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
@Slf4j
@Component
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 100;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        ClientSession session = ClientSession.getSession(ctx);
        UserDTO user = session.getUser();
        HeartBeatMsgBuilder builder = new HeartBeatMsgBuilder(user,session);
        ProtoMsg.Message message = builder.buildMsg();
        heartBeat(ctx,message);
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx, ProtoMsg.Message heartbeatMsg) {
        ctx.executor().schedule(() -> {

            if (ctx.channel().isActive()) {
                log.info(" 发送 HEART_BEAT  消息 to server");
                ctx.writeAndFlush(heartbeatMsg);

                //递归调用，发送下一次的心跳
                heartBeat(ctx, heartbeatMsg);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

}
