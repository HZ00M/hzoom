package com.hzoom.game.message.bird;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
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
