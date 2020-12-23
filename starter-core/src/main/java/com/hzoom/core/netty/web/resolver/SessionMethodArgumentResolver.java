package com.hzoom.core.netty.web.resolver;

import com.hzoom.core.netty.web.core.Session;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

import static com.hzoom.core.netty.web.endpoint.EndpointServer.CHANNEL_KEY;
public class SessionMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Session.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        Session session = channel.attr(CHANNEL_KEY).get();
        return session;
    }
}
