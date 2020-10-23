package com.example.core.netty.web.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OnlyGetHandler extends AbstractHandler{
    protected static ByteBuf forbiddenByteBuf = null;
    static{
        forbiddenByteBuf = buildStaticRes("/public/error/403.html");
        if (forbiddenByteBuf == null) {
            forbiddenByteBuf = buildStaticRes("/public/error/4xx.html");
        }
    }
    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) {
        if (req.method() != GET) {
            if (forbiddenByteBuf != null) {
                resp = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, forbiddenByteBuf.retainedDuplicate());
            } else {
                resp = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
            }
            sendHttpResponse(ctx, req, resp);
            return;
        }
        chain.doFilter(ctx,req,chain);
    }
}
