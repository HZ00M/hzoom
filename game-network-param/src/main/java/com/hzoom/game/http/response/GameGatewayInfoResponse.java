package com.hzoom.game.http.response;

import lombok.Data;

@Data
public class GameGatewayInfoResponse {
    private int id;
    private String ip;
    private int port;
    private String token;//连接此网关认证时需要的token.
    private String rsaPrivateKey;
}
