package com.hzoom.game.client;

import com.hzoom.game.config.GameClientProperties;
import com.hzoom.game.http.HttpApi;
import com.hzoom.game.http.common.BaseResponse;
import com.hzoom.game.http.request.SelectGameGatewayParam;
import com.hzoom.game.http.response.GameGatewayInfoResponse;
import com.hzoom.game.message.DispatchMessageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GameClientInitService {
    @Autowired
    private GameClientProperties gameClientProperties;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private HttpApi httpApi;

    @PostConstruct
    public void init() {
        DispatchMessageManager.scanGameMessages(applicationContext, 0, "com.hzoom");
        this.selectGateway();
    }

    private void selectGateway() {
        if (gameClientProperties.isUseGameCenter()) {
            SelectGameGatewayParam param = new SelectGameGatewayParam();
            param.setOpenId("123456");
            param.setPlayerId(123);
            param.setUserId(123);
            param.setZoneId("1");
            GameGatewayInfoResponse gateGatewayMsg = this.selectGatewayInfoFromGameCenter(param);
            if (gateGatewayMsg != null) {
                gameClientProperties.setDefaultGameGatewayHost(gateGatewayMsg.getIp());
                gameClientProperties.setDefaultGameGatewayPort(gateGatewayMsg.getSocketPort());
                gameClientProperties.setGatewayToken(gateGatewayMsg.getToken());
                gameClientProperties.setRsaPrivateKey(gateGatewayMsg.getRsaPrivateKey());
            } else {
                //todo
            }
        }
    }

    private GameGatewayInfoResponse selectGatewayInfoFromGameCenter(SelectGameGatewayParam param) {
        BaseResponse<GameGatewayInfoResponse> response = httpApi.selectGatewayInfoFromGameCenter(param);
        if (response.getCode()==0){
            return response.getData();
        }else {
            return null;
        }
    }
}
