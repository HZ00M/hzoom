package com.hzoom.game.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TopicDefine {
    String GATEWAY_TOPIC = "gateway-game-message-topic";
    @Input(GATEWAY_TOPIC)
    SubscribableChannel gatewayTopic();

}
