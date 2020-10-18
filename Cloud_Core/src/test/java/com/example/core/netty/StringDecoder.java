package com.example.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class StringDecoder extends ReplayingDecoder<StringDecoder.Status> {
    private int length;
    private byte[] inBytes;

    public StringDecoder() {
        super(Status.parse_1);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case parse_1:
                length = in.readInt();
                inBytes = new byte[length];
                checkpoint(Status.parse_2);
                break;
            case parse_2:
                in.readBytes(inBytes, 0, length);
                out.add(inBytes.toString());
                checkpoint(Status.parse_1);
                break;
            default:
                break;
        }
    }


    enum Status {
        parse_1, parse_2;
    }
}
