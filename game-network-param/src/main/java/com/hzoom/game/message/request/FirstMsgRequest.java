package com.hzoom.game.message.request;

import com.hzoom.game.message.message.AbstractMessage;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.message.MessageMetadata;
import lombok.Getter;
import lombok.Setter;

@MessageMetadata(messageId = 10001, serviceId = 101,messageType= IMessage.MessageType.REQUEST) // 添加元数据信息
public class FirstMsgRequest extends AbstractMessage{
    @Setter
    @Getter
    private String value;

    @Override
    protected byte[] encode() {
        return value.getBytes();// 序列化消息,这里不用判断null，父类上面已判断过;
    }

    @Override
    protected void decode(byte[] body) {
        value = new String(body);// 反序列化消息，这里不用判断null，父类上面已判断过
    }

    @Override
    protected boolean isNullBody() {
        return this.value == null;
    }
}
