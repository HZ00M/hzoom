package com.hzoom.message.context;

import com.hzoom.core.stream.TopicService;
import com.hzoom.game.message.common.MessagePackage;
import com.hzoom.message.channel.GameChannelPromise;
import com.hzoom.message.channel.IMessageSendFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GatewayMessageSendFactory implements IMessageSendFactory {
    private String topic;
    private TopicService topicService;

    public GatewayMessageSendFactory(String topic, TopicService topicService) {
        this.topic = topic;
        this.topicService = topicService;
    }

    @Override
    public void sendMessage(MessagePackage gameMessagePackage, GameChannelPromise promise) {
        int toServerId = gameMessagePackage.getHeader().getToServerId();
        // 动态创建游戏网关监听消息的topic
        String sendTopic = topicService.generateTopic(topic, toServerId);
        topicService.sendMessage(gameMessagePackage.transportObject(), sendTopic);
        log.info("send topic: {} , message: {}", topic, gameMessagePackage.toString());
        promise.setSuccess();
    }
}
