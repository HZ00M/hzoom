package com.hzoom.im.handler;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.proto.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class ImProtoBufEncoder extends MessageToByteEncoder<ProtoMsg.Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMsg.Message msg, ByteBuf out) throws Exception {
        out.writeShort(ServerConstants.MAGIC_CODE);
        out.writeShort(ServerConstants.VERSION_CODE);

        // 将对象转换为byte
        byte[] msgArray = msg.toByteArray();
        // 先将消息长度写入，也就是消息头
        int length = msgArray.length;
        out.writeInt(length);
        // 消息体中包含要发送的数据
        out.writeBytes(msgArray);
    }
}
