package com.hzoom.game.controller;

import com.hzoom.common.error.GameCenterError;
import com.hzoom.common.error.IError;
import com.hzoom.common.exception.ErrorException;
import com.hzoom.common.exception.TokenException;
import com.hzoom.common.utils.JWTUtil;
import com.hzoom.game.entity.Player;
import com.hzoom.game.entity.UserAccount;
import com.hzoom.game.entity.UserAccount.ZonePlayerInfo;
import com.hzoom.game.http.mapping.GameCenterMapping;
import com.hzoom.game.http.request.CreatePlayerParam;
import com.hzoom.game.http.request.LoginParam;
import com.hzoom.game.http.response.BaseResponse;
import com.hzoom.game.http.response.LoginResponse;
import com.hzoom.game.service.PlayerService;
import com.hzoom.game.service.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.syntax.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/request")
@Slf4j
public class UserController {
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private PlayerService playerService;

    @PostMapping(GameCenterMapping.USER_LOGIN)
    public BaseResponse<LoginResponse> login(@RequestBody LoginParam param){
        param.checkParam();
        IError error = userLoginService.verfiySdkToken(param.getOpenId(), param.getSdkToken());
        if (error!=null){
            throw ErrorException.newBuilder(error).build();
        }
        UserAccount userAccount = userLoginService.login(param);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(userAccount.getUserId());
        String token = JWTUtil.getUserToken(userAccount.getOpenId(),userAccount.getUserId());
        loginResponse.setToken(token);
        log.debug("user {} 登陆成功", userAccount);
        return new BaseResponse<>(loginResponse);
    }

    @PostMapping(GameCenterMapping.CREATE_PLAYER)
    public BaseResponse<ZonePlayerInfo> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request){
        param.checkParam();
        String token = request.getHeader("token");
        if (token==null){
            throw  ErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
        }
        JWTUtil.TokenBody tokenBody;
        try {
            tokenBody = JWTUtil.getTokenBody(token);
        } catch (TokenException e) {
            throw ErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
        }
        String openId = tokenBody.getOpenId();
        UserAccount userAccount = userLoginService.getUserAccountByOpenId(openId).get();
        String zoneId = param.getZoneId();
        ZonePlayerInfo zonePlayerInfo = userAccount.getZonePlayerInfo().get(zoneId);
        if (zonePlayerInfo==null){
            Player player = playerService.createPlayer(param.getZoneId(), param.getNickName());
            zonePlayerInfo = new ZonePlayerInfo(player.getPlayerId(),System.currentTimeMillis());
            userAccount.getZonePlayerInfo().put(zoneId,zonePlayerInfo);
            userLoginService.updateUserAccount(userAccount);
        }
        return new BaseResponse<>(zonePlayerInfo);
    }
}
