package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.MessageMetadata;
import com.hzoom.game.message.message.MessageType;
import lombok.Data;

@MessageMetadata(messageId = 10002, messageType = MessageType.REQUEST, serviceId = 1)
public class SecondMsgRequest extends AbstractJsonMessage<SecondMsgRequest.SecondRequestBody> {

    @Override
    protected Class<SecondRequestBody> getBodyObjClass() {
        return SecondRequestBody.class;
    }

    @Data
    public static class SecondRequestBody {
        private String value1;
        private long value2;
        private String value3;
    }
}
