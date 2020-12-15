package com.hzoom.im.handler;

import com.hzoom.im.client.CommandController;
import com.hzoom.im.session.ClientSession;
import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.proto.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    CommandController commandController;
    @Autowired
    HeartBeatClientHandler heartBeatClientHandler;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(ProtoMsg.HeadType.LOGIN_RESPONSE)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断返回是否成功
        ProtoMsg.LoginResponse info = pkg.getLoginResponse();

        ServerConstants.ResultCodeEnum result =
                ServerConstants.ResultCodeEnum.values()[info.getCode()];

        if (!result.equals(ServerConstants.ResultCodeEnum.SUCCESS)) {
            log.info(result.getDesc());
            log.info("step3：登录Netty 服务节点失败 原因:{}",result);
        } else {
            ClientSession session = ctx.channel().attr(ClientSession.SESSION_KEY).get();
            session.setSessionId(pkg.getSessionId());
            session.setLogin(true);
            log.info("step3：登录Netty 服务节点成功");
            commandController.notifyCommandThread();
            ctx.channel().pipeline().addAfter("loginResponseHandler","heartBeatClientHandler",heartBeatClientHandler);
            ctx.channel().pipeline().remove("loginResponseHandler");
        }
    }
}
