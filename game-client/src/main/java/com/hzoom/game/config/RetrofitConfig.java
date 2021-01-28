package com.hzoom.game.config;

import com.github.lianjiatech.retrofit.spring.boot.annotation.OkHttpClientBuilder;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfig {
    @OkHttpClientBuilder
    static OkHttpClient.Builder okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS);
    }

}
