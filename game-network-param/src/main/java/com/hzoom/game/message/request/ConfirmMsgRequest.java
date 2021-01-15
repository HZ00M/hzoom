package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId = 1, serviceId = 1, messageType = IMessage.MessageType.REQUEST)
public class ConfirmMsgRequest extends AbstractJsonMessage<ConfirmMsgRequest.ConfirmBody> {

    @Override
    protected Class<ConfirmBody> getBodyObjClass() {
        return ConfirmBody.class;
    }

    @Data
    public static class ConfirmBody {
        private String token;
    }
}
