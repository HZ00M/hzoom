package com.hzoom.game.handler;

import com.hzoom.game.cloud.PlayerServiceInstanceManager;
import com.hzoom.game.stream.TopicService;
import com.hzoom.game.utils.AESUtils;
import com.hzoom.game.utils.JWTUtil;
import com.hzoom.game.utils.NettyUtils;
import com.hzoom.game.error.GatewaySocketError;
import com.hzoom.game.utils.RSAUtils;
import com.hzoom.game.common.GatewayMessageTypeEnum;
import com.hzoom.game.message.common.DefaultMessageHeader;
import com.hzoom.game.message.common.MessagePackage;
import com.hzoom.game.message.request.ConfirmMsgRequest;
import com.hzoom.game.message.request.ConnectStatusMsgRequest;
import com.hzoom.game.message.response.ConfirmMsgResponse;
import com.hzoom.game.server.GatewayServerProperties;
import com.hzoom.game.server.PlayerChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConfirmHandler extends ChannelInboundHandlerAdapter {
    private PlayerServiceInstanceManager playerServiceInstanceManager;
    private PlayerChannelManager playerChannelManager;
    private TopicService topicService;
    private GatewayServerProperties gatewayServerProperties;
    private boolean confirmsSuccess = false;
    private ScheduledFuture<?> scheduledFuture;
    @Getter
    private JWTUtil.TokenBody tokenBody;

    public ConfirmHandler(PlayerServiceInstanceManager playerServiceInstanceManager, PlayerChannelManager playerChannelManager
            , TopicService topicService, GatewayServerProperties gatewayServerProperties) {
        this.playerServiceInstanceManager = playerServiceInstanceManager;
        this.playerChannelManager = playerChannelManager;
        this.topicService = topicService;
        this.gatewayServerProperties = gatewayServerProperties;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage receivePackage = (MessagePackage) msg;
        int messageId = receivePackage.getHeader().getMessageId();
        if (messageId == GatewayMessageTypeEnum.ConnectConfirm.getMessageId()) {// 如果是认证消息，在这里处理
            ConfirmMsgRequest request = new ConfirmMsgRequest();
            request.read(receivePackage.body());
            String token = request.getBodyObj().getToken();
            ConfirmMsgResponse response = new ConfirmMsgResponse();
            if (StringUtils.isEmpty(token)) {
                log.info("token为空，断开连接");
                ctx.close();
            } else {
                JWTUtil.TokenBody tokenBody = JWTUtil.getTokenBody(token);
                confirmsSuccess = true;//标记认证成功
                repeatConnect();
                playerChannelManager.addChannel(tokenBody.getPlayerId(), ctx.channel());//加入channel管理
                String aesSecretKey = AESUtils.createSecret(tokenBody.getUserId(), tokenBody.getServerId());//生成此连接的AES密钥
                // 将对称加密密钥分别设置到编码和解码的handler中
                EncodeHandler encodeHandler = ctx.pipeline().get(EncodeHandler.class);
                DecodeHandler decodeHandler = ctx.pipeline().get(DecodeHandler.class);
                encodeHandler.setAesSecret(aesSecretKey);
                decodeHandler.setAesSecret(aesSecretKey);
                byte[] clientRsaPublicKey = getClientRsaPublicKey();
                byte[] encryptAesKey = RSAUtils.encryptByPublicKey(aesSecretKey.getBytes(), clientRsaPublicKey);// 使用客户端的公钥加密对称加密密钥

                response.getBodyObj().setAesSecretKey(Base64Utils.encodeToString(encryptAesKey));
                MessagePackage returnPackage = new MessagePackage(response);
                ctx.writeAndFlush(returnPackage);

                // 通知各个服务，某个用户连接成功
                String ip = NettyUtils.getRemoteIP(ctx.channel());
                broadcastConnectMsg(true, ctx.executor(), ip);
            }
        } else {
            if (!confirmsSuccess) {
                log.info("连接未认证，不处理任务消息，关闭连接，channelId:{}", ctx.channel().id().asShortText());
                ctx.close();
            } else {
                ctx.fireChannelRead(msg);
            }
        }

    }

    private void broadcastConnectMsg(boolean connect, EventExecutor executor, String connectIp) {
        ConnectStatusMsgRequest request = new ConnectStatusMsgRequest();
        request.getBodyObj().setConnect(connect);
        long playerId = tokenBody.getPlayerId();
        Set<Integer> allServiceId = playerServiceInstanceManager.getAllServiceId();
        //通知所有的服务，用户的连接状态
        MessagePackage messagePackage = new MessagePackage(request);
        for (Integer serviceId : allServiceId) {
            //TODO dispatch
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {// 此方法会在连接建立成功channel注册之后调用
        log.info("客户端{} 链接成功，channelId: {}", NettyUtils.getRemoteIP(ctx.channel()), ctx.channel().id().asShortText());
        int delay = gatewayServerProperties.getWaiteConfirmTimeoutSecond();
        scheduledFuture = ctx.executor().schedule(() -> {
            if (!confirmsSuccess) {
                log.error("客户端{}链接确认超时!", NettyUtils.getRemoteIP(ctx.channel()));
                ctx.close();
            }
        }, delay, TimeUnit.SECONDS);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (scheduledFuture != null) {// 如果连接关闭了，取消息定时检测任务。
            scheduledFuture.cancel(true);
        }
        if (tokenBody != null) { // 连接断开之后，移除连接
            playerChannelManager.removeChannel(tokenBody.getPlayerId(), ctx.channel());// 调用移除，否则出现内存泄漏的问题。
        }
        ctx.fireChannelInactive();
    }

    private void repeatConnect() {
        if (tokenBody != null) {
            Channel existChannel = playerChannelManager.getChannel(tokenBody.getPlayerId());
            if (existChannel != null) {//关闭旧链接，保留新链接
                ConfirmMsgResponse response = new ConfirmMsgResponse();
                DefaultMessageHeader header = (DefaultMessageHeader) response.getHeader();
                header.setErrorCode(GatewaySocketError.REPEAT_CONNECT.getErrorCode());
                MessagePackage messagePackage = new MessagePackage();
                messagePackage.setHeader(header);
                existChannel.writeAndFlush(messagePackage);
                existChannel.close();
            }
        }
    }

    // 从token中获取客户端的公钥
    private byte[] getClientRsaPublicKey() {
        String rsaPublicKey = tokenBody.getParam()[0];//获取客户端非对称的RSA公钥字符串
        return Base64Utils.decodeFromString(rsaPublicKey);
    }
}
