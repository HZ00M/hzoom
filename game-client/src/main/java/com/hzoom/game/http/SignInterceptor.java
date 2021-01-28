package com.hzoom.game.http;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import com.hzoom.game.config.GameClientProperties;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SignInterceptor extends BasePathMatchInterceptor {
    @Autowired
    private GameClientProperties gameClientProperties;

    @Override
    public Response doIntercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newReq = request.newBuilder()
                .addHeader("token", gameClientProperties.getWebToken())
                .build();
        return chain.proceed(newReq);
    }
}
