package com.example.core.netty.web.core;

import com.example.core.netty.web.endpoint.EndpointConfig;
import com.example.core.netty.web.endpoint.EndpointServer;
import com.example.core.netty.web.handler.HandShakerHandler;
import com.example.core.netty.web.handler.Handler;
import com.example.core.netty.web.handler.HttpServerHandlerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class WebsocketServer {
    private final EndpointServer endpointServer;
    private EndpointConfig config;
    private LinkedList<Handler> beforeHandShakeHandlers = new LinkedList<>();

    public WebsocketServer(EndpointServer endpointServer, EndpointConfig endpointConfig, Class<? extends Handler>[] handlerClazzArr) {
        this.endpointServer = endpointServer;
        this.config = endpointConfig;
        buildChain(handlerClazzArr);
    }

    public void init() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(config.getBOSS_LOOP_GROUP_THREADS());
        EventLoopGroup worker = new NioEventLoopGroup(config.getWORKER_LOOP_GROUP_THREADS());
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getCONNECT_TIMEOUT_MILLIS())
                .option(ChannelOption.SO_BACKLOG, config.getSO_BACKLOG())
                .childOption(ChannelOption.WRITE_SPIN_COUNT, config.getWRITE_SPIN_COUNT())
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(config.getWRITE_BUFFER_LOW_WATER_MARK(), config.getWRITE_BUFFER_HIGH_WATER_MARK()))
                .childOption(ChannelOption.TCP_NODELAY, config.isTCP_NODELAY())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isSO_KEEPALIVE())
                .childOption(ChannelOption.SO_LINGER, config.getSO_LINGER())
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, config.isALLOW_HALF_CLOSURE())
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new HttpServerHandlerManager(endpointServer,config,beforeHandShakeHandlers));
                    }
                });
        if (config.getSO_RCVBUF() != -1) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, config.getSO_RCVBUF());
        }
        if (config.getSO_SNDBUF() != -1) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, config.getSO_SNDBUF());
        }

        ChannelFuture channelFuture;
        if ("0.0.0.0".equals(config.getHOST())) {
            channelFuture = bootstrap.bind(config.getPORT());
        } else {
            try {
                channelFuture = bootstrap.bind(new InetSocketAddress(InetAddress.getByName(config.getHOST()), config.getPORT()));
            } catch (UnknownHostException e) {
                channelFuture = bootstrap.bind(config.getHOST(), config.getPORT());
                e.printStackTrace();
            }
        }

        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }));
    }

    private void buildChain(Class<? extends Handler>[] handlerClazzArr)  {
        try {
            for (Class<? extends Handler> handlerClazz : handlerClazzArr) {
                Handler handler = handlerClazz.newInstance();
                beforeHandShakeHandlers.addLast(handler);
            }
        }catch (IllegalAccessException| InstantiationException e){

        }finally {
            beforeHandShakeHandlers.addLast(new HandShakerHandler());
        }

    }

    public EndpointServer getEndpointServer() {
        return endpointServer;
    }
}
