package com.example.core.netty.web.resolver;

import com.example.core.netty.web.annotation.ServerListener;
import com.example.core.netty.web.enums.ListenerTypeEnum;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.core.MethodParameter;

import java.util.Objects;

public class TextMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(ServerListener.class) && Objects.requireNonNull(parameter.getMethodAnnotation(ServerListener.class)).value().equals(ListenerTypeEnum.OnMessage) && String.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        TextWebSocketFrame textFrame = (TextWebSocketFrame) object;
        return textFrame.text();
    }
}
