package com.hzoom.game.message.bird.rpc;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;

@MessageMetadata(messageId = 210, messageType = IMessage.MessageType.RPC_RESPONSE, serviceId = 102)
public class ConsumeDiamondRPCResponse extends AbstractJsonMessage<ConsumeDiamondRPCResponse.ResponseBody> {

    public static class ResponseBody {
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
