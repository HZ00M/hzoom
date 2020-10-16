package com.example.core.netty.web.resolver;

import com.example.core.netty.web.core.WebSocketChannel;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

import static com.example.core.netty.web.endpoint.EndpointServer.CHANNEL_KEY;
public class SessionMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return WebSocketChannel.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        WebSocketChannel wsChannel = channel.attr(CHANNEL_KEY).get();
        return wsChannel;
    }
}
