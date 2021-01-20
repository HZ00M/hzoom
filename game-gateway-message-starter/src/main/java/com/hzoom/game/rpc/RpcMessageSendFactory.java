package com.hzoom.game.rpc;

import com.hzoom.game.cloud.PlayerServiceInstanceManager;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.stream.TopicService;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcMessageSendFactory {
    private AtomicInteger seqId = new AtomicInteger();// 自增的唯一序列Id
    private int localServerId;// 本地服务实例ID

    private PlayerServiceInstanceManager playerServiceInstanceManager;
    private EventExecutorGroup eventExecutorGroup;
    private TopicService topicService;
    private RpcCallbackManager rpcCallbackManager;
    private String requestTopic;
    private String responseTopic;

    public RpcMessageSendFactory(String requestTopic, String responseTopic, int localServerId, PlayerServiceInstanceManager playerServiceInstanceManager, EventExecutorGroup eventExecutorGroup, TopicService topicService) {
        this.localServerId = localServerId;
        this.requestTopic = requestTopic;
        this.responseTopic = responseTopic;
        this.playerServiceInstanceManager = playerServiceInstanceManager;
        this.eventExecutorGroup = eventExecutorGroup;
        this.topicService = topicService;
        this.rpcCallbackManager = new RpcCallbackManager(eventExecutorGroup);
    }

    public void sendRPCRequest(IMessage message, Promise<IMessage> promise) {
        MessagePackage messagePackage = new MessagePackage(message);
        Promise<Integer> selectServerIdPromise = new DefaultPromise<>(this.eventExecutorGroup.next());
        playerServiceInstanceManager
                .selectServerId(message.getHeader().getPlayerId(), message.getHeader().getServiceId(), selectServerIdPromise)
                .addListener((Future<Integer> f) -> {
                    if (f.isSuccess()) {
                        Integer toServerId = f.get();
                        String sendTopic = topicService.generateTopic(requestTopic, toServerId);
                        topicService.sendMessage(messagePackage.transportObject(), requestTopic);
                        rpcCallbackManager.addCallback(message.getHeader().getClientSeqId(), promise);
                    } else {
                        log.error("获取目标服务实例ID出错", f.cause());
                    }
                });
    }

    public void sendRPCResponse(IMessage message) {
        MessagePackage gameMessagePackage = new MessagePackage(message);
        String sendTopic = topicService.generateTopic(responseTopic, message.getHeader().getToServerId());
        topicService.sendMessage(gameMessagePackage.transportObject(),responseTopic);
    }

    public void receiveResponse(IMessage gameMessage) {
        rpcCallbackManager.callback(gameMessage);
    }

}
