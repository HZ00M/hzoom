package com.example.core.netty.web.handler;

import com.example.core.netty.web.endpoint.EndpointConfig;
import com.example.core.netty.web.endpoint.EndpointServer;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;


public class HttpServerHandlerManager extends SimpleChannelInboundHandler<FullHttpRequest> {
    private EndpointServer endpointServer;
    private EndpointConfig config;
    private HandlerChain handlerChain;

    public HttpServerHandlerManager(EndpointServer endpointServer, EndpointConfig config) {
        this.endpointServer = endpointServer;
        this.config = config;
        this.handlerChain = new HandlerChain(endpointServer, config);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        buildChain();
        handleHttpRequest(ctx, request);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        handlerChain.doFilter(ctx, req, handlerChain);
    }

    protected void buildChain() {
        handlerChain.addFilter(new BadRequestHandler())
                .addFilter(new OnlyGetHandler())
                .addFilter(new CheckHostHandler())
                .addFilter(new ResourceHandler())
                .addFilter(new PathHandler())
                .addFilter(new UpGradeHandler())
                .addFilter(new HandShakerHandler());
    }

}
