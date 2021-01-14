package com.hzoom.game.message.response;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.MessageMetadata;
import com.hzoom.game.message.message.MessageType;
import lombok.Data;

@MessageMetadata(messageId = 10002, serviceId = 1, messageType = MessageType.RESPONSE)
public class SecondMsgResponse extends AbstractJsonMessage<SecondMsgResponse.SecondMsgResponseBody> {

    @Override
    protected Class<SecondMsgResponseBody> getBodyObjClass() {
        return SecondMsgResponseBody.class;
    }

    @Data
    public static class SecondMsgResponseBody {
        private long result1;
        private String result2;
    }
}