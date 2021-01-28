package com.hzoom.game.message.request;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId = 10002, serviceId = 101, messageType = IMessage.MessageType.REQUEST)
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
