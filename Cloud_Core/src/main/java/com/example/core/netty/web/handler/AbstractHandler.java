package com.example.core.netty.web.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

import java.io.InputStream;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public abstract class AbstractHandler implements Handler{
    protected FullHttpResponse resp;

    static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp) {
        // Generate an error page if response getStatus code is not OK (200).
        int statusCode = resp.status().code();
        if (statusCode != OK.code() && resp.content().readableBytes() == 0) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
        }
        HttpUtil.setContentLength(resp, resp.content().readableBytes());

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(resp);
        if (!HttpUtil.isKeepAlive(req) || statusCode != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

    }

    static ByteBuf buildStaticRes(String resPath) {
        try {
            InputStream inputStream = HttpServerHandlerManager.class.getResourceAsStream(resPath);
            if (inputStream != null) {
                int available = inputStream.available();
                if (available != 0) {
                    byte[] bytes = new byte[available];
                    inputStream.read(bytes);
                    return ByteBufAllocator.DEFAULT.buffer(bytes.length).writeBytes(bytes);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
