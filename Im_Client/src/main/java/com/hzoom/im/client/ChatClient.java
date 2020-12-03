package com.hzoom.im.client;

import com.hzoom.im.handler.ImProtoBufDecoder;
import com.hzoom.im.handler.ImProtoBufEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatClient {
    @Value("${remote.host}")
    private String host;
    @Value("${remote.port}")
    private int port;
    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    private GenericFutureListener<ChannelFuture> connectedListener;

    public void doConnect() {
        try {
            bootstrap.remoteAddress(host,port)
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new ImProtoBufDecoder());
                            ch.pipeline().addLast("encoder", new ImProtoBufEncoder());
                        }
                    });
            ChannelFuture f = bootstrap.connect();
            f.addListener(connectedListener);
        }catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
            close();
        }
    }

    public GenericFutureListener<ChannelFuture> getConnectedListener() {
        return connectedListener;
    }

    public void setConnectedListener(GenericFutureListener<ChannelFuture> connectedListener) {
        this.connectedListener = connectedListener;
    }

    public void close() {
        group.shutdownGracefully();
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
