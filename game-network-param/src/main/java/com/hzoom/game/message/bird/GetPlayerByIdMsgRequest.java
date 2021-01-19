package com.hzoom.game.message.bird;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId = 202, messageType = IMessage.MessageType.REQUEST, serviceId = 101)
public class GetPlayerByIdMsgRequest extends AbstractJsonMessage<GetPlayerByIdMsgRequest.RequestBody> {
    @Data
    public static class RequestBody {
        private int playerId;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
