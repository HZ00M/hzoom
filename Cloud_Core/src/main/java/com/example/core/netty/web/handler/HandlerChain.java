package com.example.core.netty.web.handler;

import com.example.core.netty.web.endpoint.EndpointConfig;
import com.example.core.netty.web.endpoint.EndpointServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.springframework.beans.TypeMismatchException;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HandlerChain extends AbstractHandler {
    private List<Handler> filters = new ArrayList<Handler>();
    private int index;
    protected EndpointServer endpointServer;
    protected EndpointConfig config;

    public HandlerChain(EndpointServer endpointServer, EndpointConfig config) {
        this.endpointServer = endpointServer;
        this.config = config;
    }

    public HandlerChain addFilter(Handler f) {
        filters.add(f);
        return this;
    }

    public HandlerChain addFilters(List<Handler> f) {
        filters.addAll(f);
        return this;
    }

    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, HandlerChain chain) {
        if (index == filters.size()) return;
        Handler filter = filters.get(index);
        index++;
        try {
            filter.doFilter(ctx, req, this);
        }catch (TypeMismatchException e) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            sendHttpResponse(ctx, req, res);
            e.printStackTrace();
        } catch (Exception e) {
            FullHttpResponse res;
            if (internalServerErrorByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, internalServerErrorByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
            }
            sendHttpResponse(ctx, req, res);
            e.printStackTrace();
        }
    }
}
