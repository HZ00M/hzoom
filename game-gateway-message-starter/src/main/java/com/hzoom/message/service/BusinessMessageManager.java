package com.hzoom.message.service;

import com.hzoom.core.stream.TopicService;
import com.hzoom.game.cloud.PlayerServiceInstanceManager;
import com.hzoom.game.concurrent.GameEventExecutorGroup;
import com.hzoom.game.message.GameMessageManager;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessagePackage;
import com.hzoom.message.channel.GameChannelInitializer;
import com.hzoom.message.channel.GameMessageEventDispatchService;
import com.hzoom.message.channel.IMessageSendFactory;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.message.context.GatewayMessageSendFactory;
import com.hzoom.message.rpc.RpcMessageSendFactory;
import com.hzoom.message.stream.BusinessSink;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ApplicationContext;

/**
 *  用于接收网关消息，并分发消息到业务中。
 */
@Slf4j
public class BusinessMessageManager {
    @Autowired
    private ChannelServerProperties channelServerProperties;
    @Autowired
    private GameMessageManager gameMessageManager;
    @Autowired
    private TopicService topicService;
    @Autowired
    private PlayerServiceInstanceManager playerServiceInstanceManager;
    @Autowired
    private ApplicationContext applicationContext;

    private IMessageSendFactory messageSendFactory;

    private RpcMessageSendFactory rpcMessageSendFactory;

    private GameMessageEventDispatchService messageEventDispatchService;

    private GameEventExecutorGroup workerGroup;// 业务处理的线程池

    private EventExecutorGroup rpcWorkerGroup = new DefaultEventExecutorGroup(2);

    public void start(GameChannelInitializer gameChannelInitializer, int localServerId) {
        workerGroup = new GameEventExecutorGroup(channelServerProperties.getWorkerThreads());
        messageSendFactory = new GatewayMessageSendFactory(channelServerProperties.getGatewayGameMessageTopic(),topicService);
        rpcMessageSendFactory = new RpcMessageSendFactory(channelServerProperties.getRpcRequestGameMessageTopic(), channelServerProperties.getRpcResponseGameMessageTopic(),localServerId, playerServiceInstanceManager, rpcWorkerGroup, topicService);
        messageEventDispatchService = new GameMessageEventDispatchService(applicationContext,workerGroup, messageSendFactory, rpcMessageSendFactory, gameChannelInitializer);
    }

    @StreamListener(BusinessSink.business)
    public void consumeGameMessage(byte[] payload) {
        MessagePackage messagePackage = MessagePackage.readMessagePackage(payload);
        IMessage message = getMessage(IMessage.MessageType.REQUEST, messagePackage);
        log.info("consumeGameMessage called！message: {}",messagePackage);
        messageEventDispatchService.fireReadMessage(messagePackage.getHeader().getPlayerId(), message);
    }

    @StreamListener(BusinessSink.rpcRequest)
    public void consumeRPCRequestMessage(byte[] payload) {
        MessagePackage messagePackage = MessagePackage.readMessagePackage(payload);
        IMessage message = getMessage(IMessage.MessageType.RPC_REQUEST,messagePackage);
        log.info("consumeRPCRequestMessage called！message: {}",messagePackage);
        messageEventDispatchService.fireReadRPCRequest(message);
    }

    @StreamListener(BusinessSink.rpcResponse)
    public void consumeRPCResponseMessage(byte[] payload) {
        MessagePackage messagePackage = MessagePackage.readMessagePackage(payload);
        IMessage message = getMessage(IMessage.MessageType.RPC_RESPONSE, messagePackage);
        log.info("consumeRPCResponseMessage called！message: {}",messagePackage);
        rpcMessageSendFactory.receiveResponse(message);
    }

    private IMessage getMessage(IMessage.MessageType messageType, MessagePackage messagePackage) {
        IMessage.Header header = messagePackage.getHeader();
        IMessage message = gameMessageManager.getMessageInstance( header.getMessageId(),messageType);
        message.read(messagePackage.body());
        message.setHeader(header);
        message.getHeader().setMessageType(messageType);
        return message;
    }
}
