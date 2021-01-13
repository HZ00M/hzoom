package com.hzoom.game.message.message;

public interface IMessageHeader extends Cloneable{
    int getMessageId();
    int getServiceId();
    MessageType getMessageType();
}
