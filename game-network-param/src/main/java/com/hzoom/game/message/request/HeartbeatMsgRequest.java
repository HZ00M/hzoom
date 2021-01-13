package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.MessageMetadata;
import com.hzoom.game.message.message.MessageType;

@MessageMetadata(messageId=2,messageType= MessageType.REQUEST,serviceId=1)
public class HeartbeatMsgRequest extends AbstractJsonMessage<Void> {

    @Override
    protected Class<Void> getBodyObjClass() {
        return null;
    }

}