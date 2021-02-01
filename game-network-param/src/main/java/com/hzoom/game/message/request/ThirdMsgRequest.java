package com.hzoom.game.message.request;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hzoom.game.message.common.AbstractMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import com.hzoom.game.proto.GameProtoMsg;
import lombok.Getter;
import lombok.Setter;

@MessageMetadata(messageId = 10003, serviceId = 101, messageType = IMessage.MessageType.REQUEST)
public class ThirdMsgRequest extends AbstractMessage {
    @Setter
    @Getter
    private GameProtoMsg.FirstBodyRequest request;

    @Override
    protected byte[] encode() {
        return request.toByteArray();
    }

    @Override
    protected void decode(byte[] body) {
        try {
            GameProtoMsg.FirstBodyRequest.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isNullBody() {
        return request == null;
    }
}
