package com.example.core.netty.web.handler;

import com.example.core.netty.web.resolver.WsPathMatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class PathHandler extends AbstractHandler{
    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) {
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
    }
}
