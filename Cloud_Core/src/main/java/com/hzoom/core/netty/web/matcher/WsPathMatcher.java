package com.hzoom.core.netty.web.matcher;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;

public interface WsPathMatcher {
    String getPattern();

    boolean matchAndExtract(QueryStringDecoder decoder, Channel channel);
}
