package com.hzoom.game.message.bird;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;

@MessageMetadata(messageId = 203, messageType = IMessage.MessageType.REQUEST, serviceId = 101)
public class GetArenaPlayerListMsgRequest extends AbstractJsonMessage<GetArenaPlayerListMsgRequest.RequestBody> {
    public static class RequestBody {
        
    }
    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
