package com.hzoom.game.service;

import com.hzoom.game.channel.GameChannelInitializer;
import com.hzoom.game.channel.GameMessageEventDispatchService;
import com.hzoom.game.channel.IMessageSendFactory;
import com.hzoom.game.cloud.PlayerServiceInstanceManager;
import com.hzoom.game.concurrent.GameEventExecutorGroup;
import com.hzoom.game.config.GameChannelProperties;
import com.hzoom.game.context.GatewayMessageSendFactory;
import com.hzoom.game.message.GameMessageManager;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.rpc.RpcMessageSendFactory;
import com.hzoom.game.stream.TopicService;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 *  用于接收网关消息，并分发消息到业务中。
 */
@Service
@Slf4j
public class GatewayMessageConsumerService {
    @Autowired
    private GameChannelProperties gameChannelProperties;
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
        workerGroup = new GameEventExecutorGroup(gameChannelProperties.getWorkerThreads());
        messageSendFactory = new GatewayMessageSendFactory(gameChannelProperties.getGatewayGameMessageTopic(),topicService);
        rpcMessageSendFactory = new RpcMessageSendFactory(gameChannelProperties.getRpcRequestGameMessageTopic(),gameChannelProperties.getRpcResponseGameMessageTopic(),localServerId, playerServiceInstanceManager, rpcWorkerGroup, topicService);
        messageEventDispatchService = new GameMessageEventDispatchService(applicationContext,workerGroup, messageSendFactory, rpcMessageSendFactory, gameChannelInitializer);
        workerGroup = new GameEventExecutorGroup(gameChannelProperties.getWorkerThreads());
    }

    @StreamListener()
    public void receive(String message){
        log.info("StreamConsumer receive : "+message);
    }

    @StreamListener(target = "${game.channel.business-game-message-topic}" + "-" + "${game.server.config.server-id}")
    public void consume(MessagePackage messagePackage) {
        IMessage message = getMessage(IMessage.MessageType.REQUEST, messagePackage);
        messageEventDispatchService.fireReadMessage(messagePackage.getHeader().getPlayerId(), message);
    }

    @StreamListener(target = "${game.channel.rpc-request-game-message-topic}" + "-" + "${game.server.config.server-id}")
    public void consumeRPCRequestMessage(MessagePackage messagePackage) {
        IMessage message = getMessage(IMessage.MessageType.RPC_REQUEST,messagePackage);
        messageEventDispatchService.fireReadRPCRequest(message);
    }

    @StreamListener(target = "${game.channel.rpc-response-game-message-topic}" + "-" + "${game.server.config.server-id}")
    public void consumeRPCResponseMessage(MessagePackage messagePackage) {
        IMessage message = getMessage(IMessage.MessageType.RPC_RESPONSE, messagePackage);
        rpcMessageSendFactory.receiveResponse(message);
    }

    private IMessage getMessage(IMessage.MessageType messageType, MessagePackage messagePackage) {
        log.debug("收到{}消息：{}", messageType, messagePackage.getHeader());
        IMessage.Header header = messagePackage.getHeader();
        IMessage message = gameMessageManager.getMessageInstance( header.getMessageId(),messageType);
        message.read(messagePackage.body());
        message.setHeader(header);
        message.getHeader().setMessageType(messageType);
        return message;
    }
}
