package com.hzoom.game.message.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessagePackage implements IMessage{
    private final static int HEADER_FIX_LEN = 60;
    private Header header;
    private byte[] body;

    public MessagePackage(){
    }
    public byte[] transportObject(){
        int initialCapacity = HEADER_FIX_LEN;
        if (body != null) {
            initialCapacity += body.length;
        }
        ByteBuf headerBuf = Unpooled.buffer(initialCapacity);
        headerBuf.writeInt(initialCapacity);
        headerBuf.writeInt(header.getToServerId());
        headerBuf.writeInt(header.getFromServerId());
        headerBuf.writeInt(header.getClientSeqId());
        headerBuf.writeInt(header.getServiceId());
        headerBuf.writeInt(header.getMessageId());
        headerBuf.writeInt(header.getVersion());
        headerBuf.writeLong(header.getClientSendTime());
        headerBuf.writeLong(header.getServerSendTime());
        headerBuf.writeLong(header.getPlayerId());
        headerBuf.writeInt(header.getErrorCode());
        byte[] value = null;
        if (body != null) {
            ByteBuf bodyBuf = Unpooled.wrappedBuffer(body);//使用byte[]包装为ByteBuf，减少一次byte[]拷贝。
            ByteBuf packageBuf = Unpooled.wrappedBuffer(headerBuf, bodyBuf);
            value = new byte[packageBuf.readableBytes()];
            packageBuf.readBytes(value);
        } else {
            value = headerBuf.array();
        }
        return value;
    }

    public MessagePackage(IMessage message){
        this.header = message.getHeader();
        this.body = message.body();
    }

    public static MessagePackage readMessagePackage(byte[] value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(value);//直接使用byte[]包装为ByteBuf，减少一次数据复制
        int messageSize = byteBuf.readInt();//依次读取包头信息
        int toServerId = byteBuf.readInt();
        int fromServerId = byteBuf.readInt();
        int clientSeqId = byteBuf.readInt();
        int serviceId = byteBuf.readInt();
        int messageId = byteBuf.readInt();
        int version = byteBuf.readInt();
        long clientSendTime = byteBuf.readLong();
        long serverSendTime = byteBuf.readLong();
        long playerId = byteBuf.readLong();
        int errorCode = byteBuf.readInt();
        byte[] body = null;
        if(byteBuf.readableBytes() > 0) {
            body = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(body);
        }
        Header header = new IMessage.Header();//向包头对象中添加数据
        header.setClientSendTime(clientSendTime);
        header.setClientSeqId(clientSeqId);
        header.setErrorCode(errorCode);
        header.setFromServerId(fromServerId);
        header.setMessageId(messageId);
        header.setMessageSize(messageSize);
        header.setPlayerId(playerId);
        header.setServerSendTime(serverSendTime);
        header.setServiceId(serviceId);
        header.setToServerId(toServerId);
        header.setVersion(version);
        MessagePackage messagePackage = new MessagePackage();//创建消息对象
        messagePackage.setHeader(header);
        messagePackage.setBody(body);
        return messagePackage;
    }

    @Override
    public void read(byte[] messagePackageBytes) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(messagePackageBytes);//直接使用byte[]包装为ByteBuf，减少一次数据复制
        int messageSize = byteBuf.readInt();//依次读取包头信息
        int toServerId = byteBuf.readInt();
        int fromServerId = byteBuf.readInt();
        int clientSeqId = byteBuf.readInt();
        int messageId = byteBuf.readInt();
        int serviceId = byteBuf.readInt();
        int version = byteBuf.readInt();
        long clientSendTime = byteBuf.readLong();
        long serverSendTime = byteBuf.readLong();
        long playerId = byteBuf.readLong();
        int errorCode = byteBuf.readInt();
        byte[] body = null;
        if(byteBuf.readableBytes() > 0) {
            body = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(body);
        }
        header.setClientSendTime(clientSendTime);
        header.setClientSeqId(clientSeqId);
        header.setErrorCode(errorCode);
        header.setFromServerId(fromServerId);
        header.setMessageId(messageId);
        header.setMessageSize(messageSize);
        header.setPlayerId(playerId);
        header.setServerSendTime(serverSendTime);
        header.setServiceId(serviceId);
        header.setToServerId(toServerId);
        header.setVersion(version);
        this.body = body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] body() {
        return body;
    }


    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public MessageType getMessageType() {
        return header.getMessageType();
    }

    @Override
    public String toString() {
        return "MessagePackage{" +
                "header=" + header.toString() +
                '}';
    }
}
