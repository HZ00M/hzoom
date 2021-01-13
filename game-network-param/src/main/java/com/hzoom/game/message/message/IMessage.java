package com.hzoom.game.message.message;

public interface IMessage {
    IMessageHeader getHeader();

    void setHeader(IMessageHeader messageHeader);

    void read(byte[] body);

    byte[] body();
}
