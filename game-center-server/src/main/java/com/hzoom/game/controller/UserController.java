package com.hzoom.game.controller;

import com.hzoom.common.error.IError;
import com.hzoom.common.exception.ErrorException;
import com.hzoom.common.utils.JWTUtil;
import com.hzoom.game.entity.UserAccount;
import com.hzoom.game.http.mapping.GameCenterMapping;
import com.hzoom.game.http.request.LoginParam;
import com.hzoom.game.http.response.BaseResponse;
import com.hzoom.game.http.response.LoginResponse;
import com.hzoom.game.service.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@Slf4j
public class UserController {
    @Autowired
    private UserLoginService userLoginService;

    @PostMapping(GameCenterMapping.USER_LOGIN)
    public BaseResponse<LoginResponse> login(@RequestBody LoginParam loginParam){
        loginParam.checkParam();
        IError error = userLoginService.verfiySdkToken(loginParam.getOpenId(), loginParam.getSdkToken());
        if (error!=null){
            throw ErrorException.newBuilder(error).build();
        }
        UserAccount userAccount = userLoginService.login(loginParam);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(userAccount.getUserId());
        String token = JWTUtil.getUserToken(userAccount.getOpenId(),userAccount.getUserId());
        loginResponse.setToken(token);
        log.debug("user {} 登陆成功", userAccount);
        return new BaseResponse<>(loginResponse);
    }
}
