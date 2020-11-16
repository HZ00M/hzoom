package com.hzoom.im.handler;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.exception.InvalidFrameException;
import com.hzoom.im.proto.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ImProtoBufDecoder extends ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // 标记一下当前的readIndex的位置
        in.markReaderIndex();
        // 判断包头长度
        if (in.readableBytes() < 8) {// 不够包头
            return;
        }
        short magic = in.readShort();
        if (magic!= ServerConstants.MAGIC_CODE){
            String error = "客户端口令不对:" + ctx.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }
        short version = in.readShort();
        if (version<ServerConstants.VERSION_CODE){
            String error = "客户端版本过低:" + ctx.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }
        // 读取传送过来的消息的长度。
        int length = in.readInt();
        // 长度如果小于0，非法数据，关闭连接
        if (length < 0) {
            ctx.close();
        }
        // 读到的消息体长度如果小于传送过来的消息长度，半包重置读取位置
        if (length > in.readableBytes()) {
            // 重置读取位置
            in.resetReaderIndex();
            return;
        }
        byte[] msgArray;
        if (in.hasArray()){
            //堆缓冲
            ByteBuf slice = in.slice();
            msgArray = slice.array();
        }else {
            msgArray = new byte[length];
            in.readBytes(msgArray,0,length);
        }
        //字节转对象
        ProtoMsg.Message msg = ProtoMsg.Message.parseFrom(msgArray);
        if (msg!=null){
            out.add(msg);
        }
    }
}
