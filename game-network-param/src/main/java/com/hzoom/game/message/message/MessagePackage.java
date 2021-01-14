package com.hzoom.game.message.message;

public class MessagePackage implements IMessage{
    private DefaultMessageHeader header;
    private byte[] body;

    public MessagePackage(){
    }
    public MessagePackage(IMessage message){
        this.header = (DefaultMessageHeader)message.getHeader();
        this.body = message.body();
    }

    @Override
    public DefaultMessageHeader getHeader() {
        return header;
    }

    @Override
    public void setHeader(IMessageHeader header) {
        this.header = (DefaultMessageHeader) header;
    }

    @Override
    public void read(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] body() {
        return body;
    }

}
