package com.hzoom.game.message.bird;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;

@MessageMetadata(messageId = 203, messageType = IMessage.MessageType.REQUEST, serviceId = 101)
public class GetArenaPlayerListMsgRequest extends AbstractJsonMessage<GetArenaPlayerListMsgRequest.RequestBody> {
    public static class RequestBody {
        
    }
    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
