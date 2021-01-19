package com.hzoom.game.message.bird;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;

@MessageMetadata(messageId = 210, messageType = IMessage.MessageType.REQUEST, serviceId = 102)
public class BuyArenaChallengeTimesMsgRequest extends AbstractJsonMessage<BuyArenaChallengeTimesMsgRequest.RequestBody> {
    
    public static class RequestBody {
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
