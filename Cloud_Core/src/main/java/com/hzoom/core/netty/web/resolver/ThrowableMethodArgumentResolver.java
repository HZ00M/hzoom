package com.hzoom.core.netty.web.resolver;

import com.hzoom.core.netty.web.annotation.ServerMethod;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

public class ThrowableMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(ServerMethod.class) && parameter.getMethodAnnotation(ServerMethod.class).value().equals(ServerMethod.Type.OnError) && Throwable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        if (object instanceof Throwable) {
            return object;
        }
        return null;
    }
}
