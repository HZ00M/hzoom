package com.hzoom.game.message.bird.rpc;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;

@MessageMetadata(messageId = 210, messageType = IMessage.MessageType.RPC_RESPONSE, serviceId = 102)
public class ConsumeDiamondRPCResponse extends AbstractJsonMessage<ConsumeDiamondRPCResponse.ResponseBody> {

    public static class ResponseBody {
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
