package com.example.core.netty.web.resolver;

import com.example.core.netty.web.annotation.PathVariable;
import com.example.core.netty.web.annotation.RequestParam;
import com.example.core.netty.web.annotation.ServerMethod;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.core.MethodParameter;

import java.util.Objects;

public class TextMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(ServerMethod.class) &&
                Objects.requireNonNull(parameter.getMethodAnnotation(ServerMethod.class)).value().equals(ServerMethod.Type.OnMessage) &&
                !parameter.hasParameterAnnotation(PathVariable.class) &&
                !parameter.hasParameterAnnotation(RequestParam.class) &&
                String.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        TextWebSocketFrame textFrame = (TextWebSocketFrame) object;
        return textFrame.text();
    }
}
