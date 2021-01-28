package com.hzoom.game.http;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BaseGlobalInterceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TokenInterceptor extends BaseGlobalInterceptor {
    private volatile String token = "";

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = request.newBuilder()
                .addHeader("token", token).build();
        return chain.proceed(newRequest);
    }
}
