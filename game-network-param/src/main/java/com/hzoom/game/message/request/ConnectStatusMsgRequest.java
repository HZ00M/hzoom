package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractJsonMessage;
import com.hzoom.game.message.message.MessageMetadata;
import com.hzoom.game.message.message.MessageType;

@MessageMetadata(messageId = 2, messageType = MessageType.REQUEST, serviceId = 1)
public class ConnectStatusMsgRequest extends AbstractJsonMessage<ConnectStatusMsgRequest.MessageBody> {
    @Override
    protected Class<MessageBody> getBodyObjClass() {
        return MessageBody.class;
    }

    public static class MessageBody {

        private boolean connect;//true是连接成功，false是连接断开

        public boolean isConnect() {
            return connect;
        }

        public void setConnect(boolean connect) {
            this.connect = connect;
        }

    }
}
