package com.hzoom.core.netty.web.handler;

import com.hzoom.core.netty.web.endpoint.EndpointConfig;
import com.hzoom.core.netty.web.endpoint.EndpointServer;
import com.hzoom.core.netty.web.filter.Filter;
import com.hzoom.core.netty.web.filter.FilterChain;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;


public class HttpServerHandlerManager extends SimpleChannelInboundHandler<FullHttpRequest> {

    private FilterChain handlerChain;

    public HttpServerHandlerManager(EndpointServer endpointServer, EndpointConfig config, List<Filter> beforeHandShakerFilters, List<ChannelHandler> beforeWebSocketHandlers) {
        this.handlerChain = new FilterChain(endpointServer, config, beforeHandShakerFilters,beforeWebSocketHandlers);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        handleHttpRequest(ctx, request);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        handlerChain.doFilter(ctx, req, handlerChain);
    }

}
