package com.hzoom.game.message.bird;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId = 210, messageType = IMessage.MessageType.RESPONSE, serviceId = 102)
public class BuyArenaChallengeTimesMsgResponse extends AbstractJsonMessage<BuyArenaChallengeTimesMsgResponse.ResponseBody> {

    @Data
    public static class ResponseBody {
        private int times;
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
