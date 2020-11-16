package com.hzoom.core.netty.web.filter;

import com.hzoom.core.netty.web.endpoint.EndpointConfig;
import com.hzoom.core.netty.web.endpoint.EndpointServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.springframework.beans.TypeMismatchException;

import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class FilterChain extends AbstractFilter {

    private List<Filter> beforeHandShakeFilters;
    private List<ChannelHandler> beforeSocketHandlers;
    private int index;
    protected EndpointServer endpointServer;
    protected EndpointConfig config;
    protected static ByteBuf internalServerErrorByteBuf = null;

    static {
        internalServerErrorByteBuf = buildStaticRes("/public/error/5xx.html");
    }

    public FilterChain(EndpointServer endpointServer, EndpointConfig config, List<Filter> beforeHandShakeFilters, List<ChannelHandler> beforeSocketHandlers) {
        this.endpointServer = endpointServer;
        this.config = config;
        this.beforeHandShakeFilters = beforeHandShakeFilters;
        this.beforeSocketHandlers = beforeSocketHandlers;
    }


    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, FilterChain chain) {
        if (index == beforeHandShakeFilters.size()) return;
        Filter filter = beforeHandShakeFilters.get(index);
        index++;
        try {
            filter.doFilter(ctx, req, this);
        } catch (TypeMismatchException e) {
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

    public List<Filter> getBeforeHandShakeFilters() {
        return beforeHandShakeFilters;
    }

    public List<ChannelHandler> getBeforeSocketHandlers() {
        return beforeSocketHandlers;
    }

    public Filter getCurFilter() {
        return beforeHandShakeFilters.get(index);
    }
}
