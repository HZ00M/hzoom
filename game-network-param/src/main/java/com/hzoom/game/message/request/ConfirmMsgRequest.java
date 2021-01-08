package com.hzoom.game.message.request;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.MessageMetadata;
import com.hzoom.game.message.common.MessageType;

@MessageMetadata(messageId = 1, messageType = MessageType.REQUEST, serviceId = 1)
public class ConfirmMsgRequest extends AbstractJsonMessage<ConfirmMsgRequest.ConfirmBody> {

    @Override
    protected Class<ConfirmBody> getBodyObjClass() {
        return ConfirmBody.class;
    }

    public static class ConfirmBody {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
