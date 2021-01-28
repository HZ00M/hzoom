package com.hzoom.game.message.request;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;

@MessageMetadata(messageId = 2, serviceId = 1, messageType = IMessage.MessageType.REQUEST)
public class HeartbeatMsgRequest extends AbstractJsonMessage<Void> {

    @Override
    protected Class<Void> getBodyObjClass() {
        return null;
    }

}