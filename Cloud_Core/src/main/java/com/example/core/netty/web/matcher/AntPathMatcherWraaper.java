package com.example.core.netty.web.matcher;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.core.netty.web.endpoint.EndpointServer.URI_TEMPLATE;

public class AntPathMatcherWraaper extends AntPathMatcher implements WsPathMatcher {

    private String pattern;

    public AntPathMatcherWraaper(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        Map<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, decoder.path(), false, variables);
        if (result) {
            String[] pathDirs = this.tokenizePath(decoder.path());
            for (int i = 0; i < pathDirs.length; i++) {
                variables.put("{"+i+"}",pathDirs[i]);
            }
            channel.attr(URI_TEMPLATE).set(variables);
            return true;
        }
        return false;
    }
}
