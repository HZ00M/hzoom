package com.hzoom.game.message.response;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hzoom.game.message.common.AbstractMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import com.hzoom.game.proto.GameProtoMsg;
import lombok.Getter;
import lombok.Setter;

@MessageMetadata(messageId = 10003, messageType = IMessage.MessageType.RESPONSE, serviceId = 1)
public class ThirdMsgResponse extends AbstractMessage {
    @Setter
    @Getter
    private GameProtoMsg.FirstBodyResponse response;

    @Override
    protected byte[] encode() {
        return response.toByteArray();
    }

    @Override
    protected void decode(byte[] body) {
        try {
            response = GameProtoMsg.FirstBodyResponse.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isNullBody() {
        return response == null;
    }
}
