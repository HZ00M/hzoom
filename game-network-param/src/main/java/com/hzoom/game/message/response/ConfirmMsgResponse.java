package com.hzoom.game.message.response;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.MessageMetadata;
import com.hzoom.game.message.message.MessageType;
import lombok.Data;

@MessageMetadata(messageId = 1, messageType = MessageType.RESPONSE, serviceId = 1)
public class ConfirmMsgResponse extends AbstractJsonMessage<ConfirmMsgResponse.ConfirmResponseBody> {

    @Override
    protected Class<ConfirmResponseBody> getBodyObjClass() {
        return ConfirmResponseBody.class;
    }

    @Data
    public static class ConfirmResponseBody {
        private String aesSecretKey; //对称加密密钥，客户端需要使用非对称加密私钥解密才能获得。
    }
}
