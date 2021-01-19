package com.hzoom.game.message.bird;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
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
