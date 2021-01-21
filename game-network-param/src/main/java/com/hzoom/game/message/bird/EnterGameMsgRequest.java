package com.hzoom.game.message.bird;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;

@MessageMetadata(messageId=201,messageType= IMessage.MessageType.REQUEST,serviceId = 101)
public class EnterGameMsgRequest extends AbstractJsonMessage<EnterGameMsgRequest.RequestBody> {

    public static class RequestBody{
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
