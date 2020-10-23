package com.example.core.netty.web.handler;

import com.example.core.netty.web.matcher.WsPathMatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.Set;

public class HandShakerHandler extends AbstractHandler{
    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        Channel channel = ctx.channel();
        String pattern = null;
        Set<WsPathMatcher> pathMatcherSet = chain.endpointServer.getPathMatcherSet();
        for (WsPathMatcher pathMatcher : pathMatcherSet) {
            if (pathMatcher.matchAndExtract(decoder, ctx.channel())) {
                pattern = pathMatcher.getPattern();
                break;
            }
        }
        String subProtocols = null;
        if (chain.endpointServer.hasBeforeHandshake(channel, pattern)) {
            chain.endpointServer.doBeforeHandshake(channel, req, pattern);
            if (!channel.isActive()) {
                return;
            }

            AttributeKey<String> subProtocolsAttrKey = AttributeKey.valueOf("subProtocols");
            if (channel.hasAttr(subProtocolsAttrKey)) {
                subProtocols = ctx.channel().attr(subProtocolsAttrKey).get();
            }
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), subProtocols, true,chain.config.getMAX_FRAME_PAYLOAD_LENGTH());
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
        }else {
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.remove(ctx.name());
            if (chain.config.getREADER_IDLE_TIME_SECONDS() != 0 || chain.config.getWRITER_IDLE_TIME_SECONDS() != 0 || chain.config.getALL_IDLE_TIME_SECONDS() != 0) {
                pipeline.addLast(new IdleStateHandler(chain.config.getREADER_IDLE_TIME_SECONDS(), chain.config.getWRITER_IDLE_TIME_SECONDS(), chain.config.getALL_IDLE_TIME_SECONDS()));
            }
            if (chain.config.isUSE_COMPRESSION_HANDLER()) {
                pipeline.addLast(new WebSocketServerCompressionHandler());
            }
            pipeline.addLast(new WebSocketFrameAggregator(Integer.MAX_VALUE));
//            pipeline.addLast(new WebSocketServerProtocolHandler(decoder.path(),subProtocols,false,chain.config.getMAX_FRAME_PAYLOAD_LENGTH()));
            pipeline.addLast(new WebSocketServerHandler(chain.endpointServer));
            String finalPattern = pattern;
            handshaker.handshake(channel, req).addListener(future -> {
                if (future.isSuccess()) {
                    chain.endpointServer.doOnOpen(channel, req, finalPattern);
                } else {
                    handshaker.close(channel, new CloseWebSocketFrame());
                }
            });
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
        return "ws://" + location;
    }
}
