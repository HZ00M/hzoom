package com.hzoom.game.message.bird;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import lombok.Data;

import java.util.Map;

@MessageMetadata(messageId = 202, messageType = IMessage.MessageType.RESPONSE, serviceId = 101)
public class GetPlayerByIdMsgResponse extends AbstractJsonMessage<GetPlayerByIdMsgResponse.ResponseBody> {
    @Data
    public static class ResponseBody {
        private long playerId;
        private String nickName;
        private Map<String, String> heros;


    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
