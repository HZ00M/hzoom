package com.example.demo.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class TestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        System.out.println("do nothing!");
        ctx.fireChannelRead(o);
    }
}
