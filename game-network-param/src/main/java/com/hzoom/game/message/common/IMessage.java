package com.hzoom.game.message.common;

import lombok.Data;

public interface IMessage {
    Header getHeader();

    void setHeader(Header messageHeader);

    void read(byte[] body);

    byte[] body();

    MessageType getMessageType();

    @Data
    class Header implements Cloneable{
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

        public Header(){}

        public Header(MessageMetadata messageMetadata){
            this.messageId = messageMetadata.messageId();
            this.serviceId = messageMetadata.serviceId();
            this.messageType = messageMetadata.messageType();
        }

        @Override
        public Header clone() throws CloneNotSupportedException {
            Header newHeader = (Header) super.clone();
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

    enum MessageType {
        REQUEST,        //客户端请求消息
        RESPONSE,       //客户端响应消息
        RPC_REQUEST,    //RPC请求消息
        RPC_RESPONSE    //RPC响应消息
    }
}
