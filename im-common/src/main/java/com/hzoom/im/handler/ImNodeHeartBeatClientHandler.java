package com.hzoom.im.handler;

import com.hzoom.im.proto.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class ImNodeHeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 心跳的时间间隔，单位为s
     */
    private static final int HEARTBEAT_INTERVAL = 100;

    /**
     * 在Handler被加入到Pipeline时，开始发送心跳
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message.newBuilder()
                        .setType(ProtoMsg.HeadType.HEART_BEAT)
                        .setSessionId("unknown")
                        .setSequence(-1);
        ProtoMsg.Message message = mb.buildPartial();
        ProtoMsg.MessageHeartBeat.Builder hb =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"imNode\"}")
                        .setUid("-1");
        message.toBuilder().setHeartBeat(hb).build();
        heartBeat(ctx, message);
    }

    private void heartBeat(ChannelHandlerContext ctx, ProtoMsg.Message message) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                log.info(" 发送 ImNode HEART_BEAT ");
                ctx.writeAndFlush(message);
                heartBeat(ctx, message);
            }
        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg==null||!(msg instanceof ProtoMsg.Message)){
            super.channelRead(ctx,msg);
            return;
        }

        //判断是否是心跳类型
        ProtoMsg.HeadType msgType= ((ProtoMsg.Message) msg).getType();
        if (msgType.equals(ProtoMsg.HeadType.HEART_BEAT)){
            log.info(" imNode 收到回写的 HEART_BEAT  消息");
            return;
        }else {
            super.channelRead(ctx, msg);
        }
    }

}
