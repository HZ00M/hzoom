package com.hzoom.game.http.response;

import lombok.Data;

@Data
public class SelectGameGatewayResponse {
    private int id;
    private String ip;
    private int socketPort;
    private int httpPort;
    private String token;//连接此网关认证时需要的token.
    private String rsaPrivateKey;

    @Override
    public String toString() {
        return "GameGatewayInfoResponse{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", socketPort=" + socketPort +
                ", httpPort=" + httpPort +
                ", token='" + token + '\'' +
                ", rsaPrivateKey='" + rsaPrivateKey + '\'' +
                '}';
    }
}
