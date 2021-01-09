package com.hzoom.game.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TopicDefine {
    String gameLogicTopic = "game-logic";
    @Input(gameLogicTopic)
    SubscribableChannel gameLogic();

}
