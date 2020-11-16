package com.hzoom.core.netty.web.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;


public interface Filter {

    void doFilter(ChannelHandlerContext ctx, FullHttpRequest req, FilterChain chain) ;

}
