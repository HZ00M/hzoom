package com.example.core.netty.web.resolver;

import com.example.core.netty.web.annotation.ServerListener;
import com.example.core.netty.web.enums.ListenerTypeEnum;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

public class ThrowableMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(ServerListener.class) && parameter.getMethodAnnotation(ServerListener.class).value().equals(ListenerTypeEnum.OnError) && Throwable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        if (object instanceof Throwable) {
            return object;
        }
        return null;
    }
}
