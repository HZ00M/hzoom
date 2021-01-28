package com.hzoom.core.netty.web.core;

import com.hzoom.core.netty.web.endpoint.EndpointConfig;
import com.hzoom.core.netty.web.endpoint.EndpointServer;
import com.hzoom.core.netty.web.filter.Filter;
import com.hzoom.core.netty.web.filter.HandShakeFilter;
import com.hzoom.core.netty.web.handler.HttpServerHandlerManager;
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
import java.util.List;

public class WebSocketServer {
    private final EndpointServer endpointServer;
    private EndpointConfig config;
    private List<Filter> beforeHandShakeFilters ;
    private List<ChannelHandler> beforeWebSocketHandlers;
    public WebSocketServer(EndpointServer endpointServer, EndpointConfig endpointConfig, Class<? extends Filter>[] handlerClazzArr,LinkedList<ChannelHandler> beforeWebSocketHandlers) {
        this.endpointServer = endpointServer;
        this.config = endpointConfig;
        beforeHandShakeFilters = buildChain(handlerClazzArr);
        this.beforeWebSocketHandlers = beforeWebSocketHandlers;
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
                        pipeline.addLast(new HttpObjectAggregator(config.getMAX_FRAME_PAYLOAD_LENGTH()));
                        pipeline.addLast(new HttpServerHandlerManager(endpointServer,config, beforeHandShakeFilters,beforeWebSocketHandlers));
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

    private List<Filter> buildChain(Class<? extends Filter>[] handlerClazzArr)  {
        LinkedList<Filter> filters = new LinkedList();
        try {
            for (Class<? extends Filter> handlerClazz : handlerClazzArr) {
                Filter filter = handlerClazz.newInstance();
                filters.addLast(filter);
            }
        }catch (IllegalAccessException| InstantiationException e){

        }finally {
            filters.addLast(new HandShakeFilter());
        }
        return filters;
    }

    public EndpointServer getEndpointServer() {
        return endpointServer;
    }
}
