package com.hzoom.game.channel;

import com.hzoom.game.message.message.MessagePackage;

public interface IMessageSendFactory {

    void sendMessage(MessagePackage gameMessagePackage, GameChannelPromise promise);
}
