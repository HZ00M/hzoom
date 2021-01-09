package com.hzoom.game.message.common;

public interface IMessageHeader extends Cloneable{
    int getMessageId();
    int getServiceId();
    MessageType getMessageType();
}
