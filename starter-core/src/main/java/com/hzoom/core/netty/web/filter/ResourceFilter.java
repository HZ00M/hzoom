package com.hzoom.core.netty.web.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ResourceFilter extends AbstractFilter {
    protected static ByteBuf faviconByteBuf = null;
    static {
        faviconByteBuf = buildStaticRes("/favicon.ico");
    }
    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, FilterChain chain) {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String path = decoder.path();
        if ("/favicon.ico".equals(path)) {
            if (faviconByteBuf != null) {
                resp = new DefaultFullHttpResponse(HTTP_1_1, OK, faviconByteBuf.retainedDuplicate());
            } else {
                resp = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            }
            sendHttpResponse(ctx, req, resp);
            return;
        }

        chain.doFilter(ctx,req,chain);
    }
}
