package com.hzoom.game.message.dispatcher;

import com.hzoom.game.message.message.IMessage;

public interface IChannelContext {
    void sendMessage(IMessage gameMessage);

    <T> T getRequest();

    long getPlayerId();
}
