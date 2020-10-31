package com.example.core.netty.web.resolver;

import com.alibaba.fastjson.JSONObject;
import com.example.core.netty.web.annotation.JsonParam;
import com.example.core.netty.web.annotation.ServerMethod;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;

import java.util.Objects;

public class JsonParamResolver implements MethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(ServerMethod.class) &&
                Objects.requireNonNull(parameter.getMethodAnnotation(ServerMethod.class)).value().equals(ServerMethod.Type.OnMessage) &&
                parameter.hasParameterAnnotation(JsonParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        JsonParam ann = parameter.getParameterAnnotation(JsonParam.class);
        Class<?> clazz = ann.value();
        TextWebSocketFrame textFrame = (TextWebSocketFrame) object;
        String jsonStr = textFrame.text();
        if (StringUtils.isBlank(jsonStr)){
            return null;
        }
        try {
            return JSONObject.parseObject(jsonStr,clazz);
        } catch (Exception e) {
            return null;
        }
    }

}
