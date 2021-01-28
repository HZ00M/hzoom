package com.hzoom.game.message.request;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
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
