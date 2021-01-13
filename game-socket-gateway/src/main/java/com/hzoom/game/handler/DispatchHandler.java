package com.hzoom.game.handler;

import com.hzoom.game.cloud.PlayerServiceInstanceManager;
import com.hzoom.game.config.TopicProperties;
import com.hzoom.game.stream.TopicService;
import com.hzoom.game.bus.InnerMessageCodec;
import com.hzoom.game.message.message.DefaultMessageHeader;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.server.GatewayServerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
@Slf4j
public class DispatchHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private PlayerServiceInstanceManager playerServiceInstanceManager;
    @Autowired
    private GatewayServerProperties gatewayServerProperties;
    @Autowired
    private TopicService topicService;
    @Autowired
    private TopicProperties topicProperties;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage messagePackage = (MessagePackage)msg;
        dispatchMessage(ctx,messagePackage);
    }

    public Promise dispatchMessage(ChannelHandlerContext ctx, MessagePackage messagePackage) {
        Promise<Integer> promise = new DefaultPromise<>(ctx.executor());
        playerServiceInstanceManager.selectServerId(messagePackage.getHeader().getPlayerId(), messagePackage.getHeader().getServiceId(), promise)
                .addListener((Future<Integer> future) -> {
                    if (future.isSuccess()) {
                        Integer toServerId = future.get();
                        DefaultMessageHeader header = messagePackage.getHeader();
                        header.setToServerId(toServerId);
                        header.setFromServerId(gatewayServerProperties.getServerId());
                        String topic = topicProperties.getGameTopic();
                        byte[] bytes = InnerMessageCodec.sendMessage(messagePackage);
                        topicService.sendMessage(bytes, topic);
                        log.info("发送到{}消息成功->{}",topic, messagePackage.getHeader());
                    } else {
                        log.error("消息发送失败", future.cause());
                    }
                });
        return promise;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("服务器异常，连接{}断开",ctx.channel().id().asShortText(),cause);
    }
}
