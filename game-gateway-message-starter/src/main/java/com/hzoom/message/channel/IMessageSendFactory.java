package com.hzoom.message.channel;

import com.hzoom.game.message.common.MessagePackage;

public interface IMessageSendFactory {

    void sendMessage(MessagePackage gameMessagePackage, GameChannelPromise promise);
}
