package com.hzoom.game.message.response;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.MessageMetadata;
import com.hzoom.game.message.common.MessageType;

@MessageMetadata(messageId = 2, messageType = MessageType.RESPONSE, serviceId = 1)
public class HeartbeatMsgResponse extends AbstractJsonMessage<HeartbeatMsgResponse.ResponseBody> {
    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    public static class ResponseBody {
        private long serverTime;

        public long getServerTime() {
            return serverTime;
        }

        public void setServerTime(long serverTime) {
            this.serverTime = serverTime;
        }

    }
}
