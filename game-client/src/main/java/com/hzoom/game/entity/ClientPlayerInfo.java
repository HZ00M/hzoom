package com.hzoom.game.entity;

import com.hzoom.game.http.response.SelectGameGatewayResponse;
import lombok.Data;

@Data
public class ClientPlayerInfo {
    private String userName;
    private String password;
    private long playerId;
    private String token;
    private long userId;
    private SelectGameGatewayResponse selectGameGatewayResponse;

}
