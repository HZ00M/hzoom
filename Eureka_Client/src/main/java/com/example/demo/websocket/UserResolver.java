package com.example.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.core.netty.web.resolver.MethodArgumentResolver;
import com.example.demo.po.User;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class UserResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {

        String userStr = ((TextWebSocketFrame)object).text();
        User user;
        try {
            user = JSONObject.parseObject(userStr, User.class);
        }catch (Exception e){
            return new User();
        }
        return user;
    }
}
