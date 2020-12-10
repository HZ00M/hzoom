package com.hzoom.im.handler;

import com.google.gson.reflect.TypeToken;
import com.hzoom.im.bean.Notification;
import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.LocalSession;
import com.hzoom.im.session.RemoteSession;
import com.hzoom.im.session.SessionManger;
import com.hzoom.im.utils.JsonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class RemoteNotificationHandler extends ChannelInboundHandlerAdapter {
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

        if (!type.equals(ProtoMsg.HeadType.MESSAGE_NOTIFICATION)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.MessageNotification messageNotification = pkg.getNotification();
        String json = messageNotification.getJson();
        Notification<RemoteSession> notification =JsonUtil.jsonToPojo(json,new TypeToken<Notification<RemoteSession>>(){}.getType());

        //链接成功通知
        if (notification.getType()==Notification.CONNECT_FINISHED){
            log.info("收到分布式节点连接成功通知, node={}", json);
            ctx.channel().attr(ServerConstants.NODE_KEY).set(JsonUtil.pojoToJson(notification.getData()));
        }
        //上线通知
        if (notification.getType()==Notification.SESSION_ON){
            log.info("收到用户上线通知, node={}", json);
            sessionManger.addRemoteSession(notification.getData());
        }
        //下线通知
        if (notification.getType()==Notification.SESSION_OFF){
            log.info("收到用户下线通知, node={}", json);
            RemoteSession remoteSession = notification.getData();
            sessionManger.removeRemoteSession(remoteSession.getSessionId());
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx)throws Exception{
        log.info("客户端连接断开");
        LocalSession session = LocalSession.getSession(ctx);
        if (null != session) {
            session.unbind();
        }
    }
}
