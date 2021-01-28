package com.hzoom.game.message.bird;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MessageMetadata(messageId = 203, messageType = IMessage.MessageType.RESPONSE, serviceId = 101)
public class GetArenaPlayerListMsgResponse extends AbstractJsonMessage<GetArenaPlayerListMsgResponse.ResponseBody> {

    @Data
    public static class ResponseBody {
        private List<ArenaPlayer> arenaPlayers;

    }
    @Data
    public static class ArenaPlayer {
        private long playerId;
        private String nickName;
        private Map<String, String> heros = new HashMap<>();

    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
