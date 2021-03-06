package com.hzoom.game.message.response;

import com.hzoom.game.message.common.AbstractMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

@MessageMetadata(messageId = 10001, serviceId = 101, messageType = IMessage.MessageType.RESPONSE) // 添加元数据信息
public class FirstMsgResponse extends AbstractMessage {
    @Setter
    @Getter
    private Long serverTime;//返回服务器的时间
    @Override
    public byte[] encode() {
        ByteBuf byteBuf = Unpooled.buffer(8);
        byteBuf.writeLong(serverTime);
        return byteBuf.array();
    }

    @Override
    protected void decode(byte[] body) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(body);
        this.serverTime = byteBuf.readLong();
    }

    @Override
    protected boolean isNullBody() {
        return this.serverTime == null;
    }

}
