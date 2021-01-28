package com.hzoom.game.controller;

import com.hzoom.game.entity.Player;
import com.hzoom.game.entity.UserAccount;
import com.hzoom.game.entity.UserAccount.ZonePlayerInfo;
import com.hzoom.game.error.GameCenterError;
import com.hzoom.game.error.IError;
import com.hzoom.game.exception.ErrorException;
import com.hzoom.game.exception.TokenException;
import com.hzoom.game.http.common.BaseResponse;
import com.hzoom.game.http.request.CreatePlayerParam;
import com.hzoom.game.http.request.LoginParam;
import com.hzoom.game.http.request.SelectGameGatewayParam;
import com.hzoom.game.http.response.LoginResponse;
import com.hzoom.game.http.response.SelectGameGatewayResponse;
import com.hzoom.game.mapping.GameCenterMapping;
import com.hzoom.game.model.GameGatewayInfo;
import com.hzoom.game.service.GameGatewayService;
import com.hzoom.game.service.PlayerService;
import com.hzoom.game.service.UserLoginService;
import com.hzoom.game.utils.JWTUtil;
import com.hzoom.game.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
public class UserController {
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameGatewayService gameGatewayService;

    @PostMapping(GameCenterMapping.USER_LOGIN)
    public BaseResponse<LoginResponse> login(@RequestBody LoginParam param) {
        param.checkParam();
        IError error = userLoginService.verfiySdkToken(param.getOpenId(), param.getSdkToken());
        if (error != null) {
            throw ErrorException.newBuilder(error).build();
        }
        UserAccount userAccount = userLoginService.login(param);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(userAccount.getUserId());
        String token = JWTUtil.getUserToken(userAccount.getOpenId(), userAccount.getUserId());
        loginResponse.setToken(token);
        log.debug("user {} 登陆成功", userAccount);
        return new BaseResponse<>(loginResponse);
    }

    @PostMapping(GameCenterMapping.CREATE_PLAYER)
    BaseResponse<ZonePlayerInfo> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request) {
        param.checkParam();
        String token = request.getHeader("token");
        if (token == null) {
            throw ErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
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
        if (zonePlayerInfo == null) {
            Player player = playerService.createPlayer(param.getZoneId(), param.getNickName());
            zonePlayerInfo = new ZonePlayerInfo(player.getPlayerId(), System.currentTimeMillis());
            userAccount.getZonePlayerInfo().put(zoneId, zonePlayerInfo);
            userLoginService.updateUserAccount(userAccount);
        }
        return new BaseResponse<>(zonePlayerInfo);
    }

    @PostMapping(GameCenterMapping.SELECT_GAME_GATEWAY)
    public Object selectGameGateway(@RequestBody SelectGameGatewayParam param) throws Exception {
        param.checkParam();
        long playerId = param.getPlayerId();
        GameGatewayInfo gameGatewayInfo = gameGatewayService.selectGameGatewayInfoByPlayerId(playerId);
        SelectGameGatewayResponse response = new SelectGameGatewayResponse();
        response.setId(gameGatewayInfo.getId());
        response.setIp(gameGatewayInfo.getIp());
        response.setHttpPort(gameGatewayInfo.getHttpPort());
        response.setSocketPort(gameGatewayInfo.getSocketPort());

        Map<String, Object> keyPair = RSAUtils.genKeyPair();//生成rsa公钥和私钥
        byte[] publicKeyBytes = RSAUtils.getPublicKey(keyPair);//获取公钥
        String publicKey = Base64Utils.encodeToString(publicKeyBytes);// 为了方便传输，对bytes数组进行一下base64编码
        String token = JWTUtil.getUserToken(param.getOpenId(), param.getUserId(), param.getPlayerId(), param.getZoneId(), publicKey, gameGatewayInfo.getIp());
        response.setToken(token);

        byte[] privateKeyBytes = RSAUtils.getPrivateKey(keyPair);//获取私钥
        String privateKey = Base64Utils.encodeToString(privateKeyBytes);
        response.setRsaPrivateKey(privateKey);
        log.debug("player {} 获取游戏网关信息成功：{}", playerId, response);
        BaseResponse<SelectGameGatewayResponse> responseEntity = new BaseResponse(response);
        return responseEntity;
    }
}
