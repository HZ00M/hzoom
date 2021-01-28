package com.hzoom.game.message.request;

import com.hzoom.game.message.common.AbstractJsonMessage;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.common.MessageMetadata;

@MessageMetadata(messageId = 2, serviceId = 1,messageType = IMessage.MessageType.REQUEST)
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
