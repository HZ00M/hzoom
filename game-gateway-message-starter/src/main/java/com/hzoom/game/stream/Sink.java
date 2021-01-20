package com.hzoom.game.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sink {
//    @Input("gateway-game-message-topic")
//    SubscribableChannel input1();
    @Input("business-game-message-topic")
    SubscribableChannel input2();
    @Input("rpc-request-game-message-topic")
    SubscribableChannel input3();
    @Input("rpc-response-game-message-topic")
    SubscribableChannel input4();
}
