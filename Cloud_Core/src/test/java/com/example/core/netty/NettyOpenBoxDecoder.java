package com.example.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class NettyOpenBoxDecoder {
    static String content = "测试";

    @Test
    public void test(){
        try {
            final LengthFieldBasedFrameDecoder spliter =
                    new LengthFieldBasedFrameDecoder(1024,0,4,0,0);
            ChannelInitializer initializer = new ChannelInitializer<EmbeddedChannel>() {
                @Override
                protected void initChannel(EmbeddedChannel channel) throws Exception {
                    channel.pipeline().addLast(spliter);
                    channel.pipeline().addLast(new StringDecoder());
                    channel.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel embeddedChannel = new EmbeddedChannel(initializer);
            for (int j = 0;j<=100;j++){
                ByteBuf buf = Unpooled.buffer();
                String s = j + "次发送"+content;
                byte[] bytes = s.getBytes("UTF-8");
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
                embeddedChannel.writeInbound(buf);
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
