package com.hzoom.im.server;

import com.hzoom.core.concurrent.callbackTask.FutureTaskScheduler;
import com.hzoom.im.distributed.Peer;
import com.hzoom.im.distributed.Router;
import com.hzoom.im.handler.*;
import com.hzoom.im.utils.IOUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component("chatServer")
public class ChatServer implements SmartInitializingSingleton {
    @Value("${server.port}")
    private int port;
    private NioEventLoopGroup boosGroup = new NioEventLoopGroup();
    private NioEventLoopGroup workGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    @Autowired
    private Peer peer;
    @Autowired
    private Router router;
    @Autowired
    private LoginRequestHandler loginRequestHandler;
    @Autowired
    private ChatRedirectHandler chatRedirectHandler;
    @Autowired
    private RemoteNotificationHandler remoteNotificationHandler;
    @Autowired
    private ServerExceptionHandler serverExceptionHandler;

    public void run() {
        String ip = IOUtil.getHostAddress();
        serverBootstrap.group(boosGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(ip, port))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new ImProtoBufDecoder());
                        ch.pipeline().addLast("encoder", new ImProtoBufEncoder());
                        ch.pipeline().addLast("heartBeat", new HeartBeatServerHandler());
                        ch.pipeline().addLast("loginRequest", loginRequestHandler);
                        ch.pipeline().addLast("remoteNotification", remoteNotificationHandler);
                        ch.pipeline().addLast("chatRedirect", chatRedirectHandler);
                        ch.pipeline().addLast("serverException", serverExceptionHandler);
                    }
                });

        // 通过调用sync同步方法阻塞直到绑定成功
        ChannelFuture channelFuture = null;
        boolean isStart = false;
        while (!isStart) {
            try {

                channelFuture = serverBootstrap.bind().sync();
                log.info("IM 启动, 端口为： " +
                        channelFuture.channel().localAddress());
                isStart = true;
            } catch (Exception e) {
                log.error("发生启动异常", e);
                port++;
                log.info("尝试一个新的端口：" + port);
                serverBootstrap.localAddress(new InetSocketAddress(port));
            }
        }
        FutureTaskScheduler.add(()->{
            peer.init(new InetSocketAddress(ip,port));
            router.init();
        });
        try {
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        }catch (Exception e){
            log.error("通道连接发生异常");
        }finally {
            workGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        run();
    }
}
