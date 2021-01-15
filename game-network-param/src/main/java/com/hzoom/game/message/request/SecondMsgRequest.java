package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId = 10002, serviceId = 1, messageType = IMessage.MessageType.REQUEST)
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
