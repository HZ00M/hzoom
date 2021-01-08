package com.hzoom.game.message.common;

public interface IMessageHeader extends Cloneable{
    Integer getMessageId();
    String getServiceId();
    MessageType getMessageType();
}
