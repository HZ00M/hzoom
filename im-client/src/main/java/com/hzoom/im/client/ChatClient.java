package com.hzoom.im.client;

import com.hzoom.im.handler.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatClient {
    private String host;
    private int port;
    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    private GenericFutureListener<ChannelFuture> connectedListener;

    @Autowired
    private LoginResponseHandler loginResponseHandler;
    @Autowired
    private ChatMsgHandler chatMsgHandler;
    @Autowired
    private ExceptionHandler exceptionHandler;

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
                            ch.pipeline().addLast("loginResponseHandler",loginResponseHandler);
                            ch.pipeline().addLast("chatMsgHandler",chatMsgHandler);
                            ch.pipeline().addLast("exceptionHandler",exceptionHandler);
                        }
                    });
            ChannelFuture f = bootstrap.connect();
            f.addListener(connectedListener);
//            f.channel().closeFuture().sync();
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
