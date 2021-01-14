package com.hzoom.game.http;

import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.OkHttpClientBuilder;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitScan;
import com.hzoom.game.http.common.BaseResponse;
import com.hzoom.game.http.request.SelectGameGatewayParam;
import com.hzoom.game.http.response.GameGatewayInfoResponse;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.concurrent.TimeUnit;

@RetrofitClient(baseUrl = "${game.client.config.game-center-url}",poolName = "pool1")
@Intercept(handler = TimestampInterceptor.class,include = "/**",exclude = "/api/test")
@Sign(accessKeyId = "${test.accessKeyId}", accessKeySecret = "${test.accessKeySecret}", include = {"/api/test"})
@RetrofitScan("com.hzoom")
public interface HttpApi {
    @Value("${game.client.config.game-center-url}")
    String value = null;

    @OkHttpClientBuilder
    static OkHttpClient.Builder okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS);
    }

    @POST("request/10003")
    BaseResponse<GameGatewayInfoResponse> selectGatewayInfoFromGameCenter(@Body SelectGameGatewayParam param);
}
