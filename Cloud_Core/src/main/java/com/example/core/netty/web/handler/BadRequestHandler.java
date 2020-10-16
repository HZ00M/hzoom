package com.example.core.netty.web.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class BadRequestHandler extends AbstractHandler{

    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) {
        if (!req.decoderResult().isSuccess()) {
            if (badRequestByteBuf != null) {
                resp = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST, badRequestByteBuf.retainedDuplicate());
            } else {
                resp = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            }
            sendHttpResponse(ctx, req, resp);
            return;
        }
    }
}
