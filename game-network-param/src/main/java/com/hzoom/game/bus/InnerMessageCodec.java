package com.hzoom.game.bus;

import com.hzoom.game.message.message.DefaultMessageHeader;
import com.hzoom.game.message.message.MessagePackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class InnerMessageCodec {
    private final static int HEADER_FIX_LEN = 60;

    public static byte[] sendMessage(MessagePackage messagePackage) {//todo 待优化
        int initialCapacity = HEADER_FIX_LEN;
        /**
         * 写入包头数据
         */
        DefaultMessageHeader header = messagePackage.getHeader();
        if (messagePackage.body() != null) {
            initialCapacity += messagePackage.body().length;
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
        /**
         * 写入包体数据
         */
        byte[] value = null;
        if (messagePackage.body() != null) {
            ByteBuf bodyBuf = Unpooled.wrappedBuffer(messagePackage.body());//使用byte[]包装为ByteBuf，减少一次byte[]拷贝。
            ByteBuf packageBuf = Unpooled.wrappedBuffer(headerBuf, bodyBuf);
            value = new byte[packageBuf.readableBytes()];
            packageBuf.readBytes(value);
        } else {
            value = headerBuf.array();
        }
        return value;
    }

    public static MessagePackage readMessagePackage(byte[] value) {//todo 待优化
        ByteBuf byteBuf = Unpooled.wrappedBuffer(value);//直接使用byte[]包装为ByteBuf，减少一次数据复制
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
        if (byteBuf.readableBytes() > 0) {
            body = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(body);
        }
        DefaultMessageHeader header = new DefaultMessageHeader();
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
        messagePackage.read(body);
        return messagePackage;
    }
}
