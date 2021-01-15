package com.hzoom.game.client;

import com.hzoom.game.config.GameClientProperties;
import com.hzoom.game.handler.codec.ClientDecodeHandler;
import com.hzoom.game.handler.codec.ClientEncodeHandler;
import com.hzoom.game.handler.common.DispatchGameMessageHandler;
import com.hzoom.game.handler.common.HeartbeatHandler;
import com.hzoom.game.handler.common.ResponseHandler;
import com.hzoom.game.message.GameMessageManager;
import com.hzoom.game.message.DispatchMessageService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameClientBoot {
    @Autowired
    private GameClientProperties gameClientProperties;
    @Autowired
    private GameMessageManager gameMessageManager;
    @Autowired
    private DispatchMessageService dispatchMessageService;
    @Autowired
    private ResponseHandler responseHandler;
    @Autowired
    private DispatchGameMessageHandler dispatchGameMessageHandler;
    private Bootstrap bootstrap;
    private EventLoopGroup worker;
    private Channel channel;

    public void launch() {
        if (channel != null) {
            channel.close();
        }
        bootstrap = new Bootstrap();
        worker = new NioEventLoopGroup(gameClientProperties.getWorkThreads());
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, gameClientProperties.getConnectTimeout() * 1000)
                .handler(channelInitializer());
        ChannelFuture connectFuture = bootstrap.connect(gameClientProperties.getDefaultGameGatewayHost(), gameClientProperties.getDefaultGameGatewayPort());
        channel = connectFuture.channel();
        connectFuture.addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                log.info("连接{}:{}成功!channelId:{}", gameClientProperties.getDefaultGameGatewayHost(), gameClientProperties.getDefaultGameGatewayPort(), future.channel().id().asShortText());
            } else {
                Throwable e = future.cause();
                log.error("连接失败{}", e.getMessage());
            }
        });

    }

    private ChannelInitializer channelInitializer() {
        return new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("encode", new ClientEncodeHandler(gameClientProperties));// 添加编码
                ch.pipeline().addLast("decode", new ClientDecodeHandler());// 添加解码
                ch.pipeline().addLast("response", responseHandler);//将响应消息转化为对应的响应对象
                ch.pipeline().addLast(new IdleStateHandler(150, 60, 200));//如果6秒之内没有消息写出，发送写出空闲事件，触发心跳
                ch.pipeline().addLast("heartbeat", new HeartbeatHandler());//心跳Handler
                ch.pipeline().addLast("dispatch", dispatchGameMessageHandler);// 添加逻辑处理
            }
        };
    }

    public Channel getChannel() {
        return channel;
    }
}
