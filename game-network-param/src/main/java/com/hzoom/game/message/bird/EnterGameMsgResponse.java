package com.hzoom.game.message.bird;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId=201,messageType= IMessage.MessageType.RESPONSE,serviceId=101)
public class EnterGameMsgResponse extends AbstractJsonMessage<EnterGameMsgResponse.ResponseBody> {

    @Data
    public static class ResponseBody {
        private String nickname;
        private long playerId;
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
