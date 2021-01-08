package com.hzoom.game.message.common;

public interface IMessage {
    IMessageHeader getHeader();

    void setHeader(IMessageHeader messageHeader);

    void read(byte[] body);

    byte[] body();
}
