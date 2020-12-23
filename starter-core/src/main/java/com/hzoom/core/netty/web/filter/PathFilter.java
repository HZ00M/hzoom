package com.hzoom.core.netty.web.filter;

import com.hzoom.core.netty.web.matcher.WsPathMatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class PathFilter extends AbstractFilter {
    protected static ByteBuf notFoundByteBuf = null;
    static {
        notFoundByteBuf = buildStaticRes("/public/error/404.html");
        if (notFoundByteBuf == null) {
            notFoundByteBuf = buildStaticRes("/public/error/4xx.html");
        }
    }
    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, FilterChain chain) {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        Channel channel = ctx.channel();
        String pattern = null;
        Set<WsPathMatcher> pathMatcherSet = chain.endpointServer.getPathMatcherSet();
        for (WsPathMatcher pathMatcher : pathMatcherSet) {
            if (pathMatcher.matchAndExtract(decoder, channel)) {
                pattern = pathMatcher.getPattern();
                break;
            }
        }

        if (pattern == null) {
            if (notFoundByteBuf != null) {
                resp = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, notFoundByteBuf.retainedDuplicate());
            } else {
                resp = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            }
            sendHttpResponse(ctx, req, resp);
            return;
        }

        chain.doFilter(ctx,req,chain);
    }
}
