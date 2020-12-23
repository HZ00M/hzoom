package com.hzoom.im.distributed;

import com.hzoom.im.bean.Notification;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.handler.ImNodeExceptionHandler;
import com.hzoom.im.handler.ImNodeHeartBeatClientHandler;
import com.hzoom.im.handler.ImProtoBufDecoder;
import com.hzoom.im.handler.ImProtoBufEncoder;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.protoBuilder.MsgBuilder;
import com.hzoom.im.utils.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PeerSender {
    private Channel channel;

    private volatile ImNode imNode;

    private boolean connectFlag;

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    public PeerSender(ImNode imNode) {
        this.imNode = imNode;
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    private GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.info("分布式连接已经断开.....{}", imNode.toString());
        channel = null;
        connectFlag = false;
    };

    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();

        if (!f.isSuccess()) {
            log.info("连接失败!在10s之后准备尝试重连!.....{}", imNode.toString());
            eventLoop.schedule(this::doConnect, 10, TimeUnit.SECONDS);
        } else {
            connectFlag = true;

            log.info(new Date() + "分布式节点连接成功:{}", imNode.toString());

            channel = f.channel();
            channel.closeFuture().addListener(closeListener);

            /**
             * 发送链接成功的通知
             */
            Notification<ImNode> notification = new Notification(SpringManager.getBean(Peer.class).getLocalImNode());
            notification.setType(Notification.CONNECT_FINISHED);
            String json = JsonUtil.pojoToJson(notification);
            ProtoMsg.Message pkg = MsgBuilder.buildNotification(json);
            writeAndFlush(pkg);
        }

    };

    public void doConnect() {
        String host = imNode.getHost();
        int port = imNode.getPort();
        try {
            if (bootstrap != null && bootstrap.group() == null) {
                bootstrap.group(eventLoopGroup)
                        .remoteAddress(host, port)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast("decoder", new ImProtoBufDecoder())
                                        .addLast("encoder", new ImProtoBufEncoder())
                                        .addLast("hearBeatClient", new ImNodeHeartBeatClientHandler())
                                        .addLast("NodeException", new ImNodeExceptionHandler());
                            }
                        });
                log.info(new Date() + "imNode开始连接节点:{}", imNode.toString());
                ChannelFuture future = bootstrap.connect();
                future.addListener(connectedListener);
            } else if (bootstrap.group() != null) {
                log.info("{} imNode再一次开始连接分布式节点 {}", new Date(), imNode.toString());
                ChannelFuture f = bootstrap.connect();
                f.addListener(connectedListener);
            }

        } catch (Exception e) {
            log.info("imNode客户端连接失败!" + e.getMessage());
        }
    }

    public void stopConnecting() {
        eventLoopGroup.shutdownGracefully();
        connectFlag = false;
    }

    public ChannelFuture writeAndFlush(Object pkg) {
        if (!connectFlag) {
            log.error("分布式节点未连接:{}", imNode.toString());
            return null;
        }
        return channel.writeAndFlush(pkg);
    }

    public ImNode getImNode() {
        return imNode;
    }
}
