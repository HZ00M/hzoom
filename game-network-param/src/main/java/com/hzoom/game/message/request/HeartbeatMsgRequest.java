package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;

@MessageMetadata(messageId = 2, serviceId = 1, messageType = IMessage.MessageType.REQUEST)
public class HeartbeatMsgRequest extends AbstractJsonMessage<Void> {

    @Override
    protected Class<Void> getBodyObjClass() {
        return null;
    }

}