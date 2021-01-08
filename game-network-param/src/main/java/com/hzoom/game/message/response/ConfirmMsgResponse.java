package com.hzoom.game.message.response;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.MessageMetadata;
import com.hzoom.game.message.common.MessageType;

@MessageMetadata(messageId=1,messageType= MessageType.RESPONSE,serviceId=1)
public class ConfirmMsgResponse extends AbstractJsonMessage<ConfirmMsgResponse.ConfirmResponseBody> {

    @Override
    protected Class<ConfirmResponseBody> getBodyObjClass() {
        return ConfirmResponseBody.class;
    }

    public static class ConfirmResponseBody {
        private String aesSecretKey; //对称加密密钥，客户端需要使用非对称加密私钥解密才能获得。

        public String getAesSecretKey() {
            return aesSecretKey;
        }

        public void setAesSecretKey(String aesSecretKey) {
            this.aesSecretKey = aesSecretKey;
        }
    }
}
