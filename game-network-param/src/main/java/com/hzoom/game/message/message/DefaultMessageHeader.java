package com.hzoom.game.message.message;

import lombok.Data;

@Data
public class DefaultMessageHeader implements IMessageHeader{
    private int messageSize;
    private long clientSendTime;
    private long serverSendTime;
    private int clientSeqId;
    private int version;
    private int errorCode;
    private int fromServerId;
    private int toServerId;
    private long playerId;

    private int messageId;
    private int serviceId;
    private MessageType messageType;

    public DefaultMessageHeader(){}

    public DefaultMessageHeader(MessageMetadata messageMetadata){
        this.messageId = messageMetadata.messageId();
        this.serviceId = messageMetadata.serviceId();
        this.messageType = messageMetadata.messageType();
    }

    @Override
    public IMessageHeader clone() throws CloneNotSupportedException {
        IMessageHeader newHeader = (DefaultMessageHeader) super.clone();
        return newHeader;
    }

    @Override
    public String toString() {
        return "DefaultMessageHeader{" +
                "messageSize=" + messageSize +
                ", messageId=" + messageId +
                ", serviceId=" + serviceId +
                ", clientSendTime=" + clientSendTime +
                ", serverSendTime=" + serverSendTime +
                ", clientSeqId=" + clientSeqId +
                ", version=" + version +
                ", errorCode=" + errorCode +
                ", fromServerId=" + fromServerId +
                ", toServerId=" + toServerId +
                ", playerId=" + playerId +
                ", messageType=" + messageType +
                '}';
    }

}
