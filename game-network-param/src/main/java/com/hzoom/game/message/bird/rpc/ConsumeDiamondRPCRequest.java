package com.hzoom.game.message.bird.rpc;


import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.Data;

@MessageMetadata(messageId = 210, messageType = IMessage.MessageType.RPC_REQUEST, serviceId = 101)
public class ConsumeDiamondRPCRequest extends AbstractJsonMessage<ConsumeDiamondRPCRequest.RequestBody> {

    @Data
    public static class RequestBody {
        private int count;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
