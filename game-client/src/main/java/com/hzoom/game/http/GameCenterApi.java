package com.hzoom.game.http;

import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitScan;
import com.hzoom.game.http.common.BaseResponse;
import com.hzoom.game.http.request.CreatePlayerParam;
import com.hzoom.game.http.request.LoginParam;
import com.hzoom.game.http.request.SelectGameGatewayParam;
import com.hzoom.game.http.response.LoginResponse;
import com.hzoom.game.http.response.SelectGameGatewayResponse;
import com.hzoom.game.http.response.ZonePlayerInfoResponse;
import com.hzoom.game.mapping.GameCenterMapping;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

@RetrofitClient(baseUrl = "${game.client.config.game-center-url}",poolName = "pool1")
@Intercept(handler = TimestampInterceptor.class,include = "/**")
@Sign
@RetrofitScan("com.hzoom")
public interface GameCenterApi {

    @POST(GameCenterMapping.USER_LOGIN)
    @Headers({"test:123"})
    BaseResponse<LoginResponse> login(@Body LoginParam param);

    @POST(GameCenterMapping.CREATE_PLAYER)
    BaseResponse<ZonePlayerInfoResponse> createPlayer(@Body CreatePlayerParam param);

    @POST(GameCenterMapping.SELECT_GAME_GATEWAY)
    BaseResponse<SelectGameGatewayResponse> selectGatewayInfoFromGameCenter(@Body SelectGameGatewayParam param);
}
