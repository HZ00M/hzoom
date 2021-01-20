package com.hzoom.game.model;

import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class GameGatewayInfo {
    private int id; // 唯一id
    private String ip; // 网关ip地址
    private int socketPort; // 网关端口
    private int httpPort;//网关服务的Http的服务地址

    public static GameGatewayInfo newGatewayInfo(int id, ServiceInstance instance) {
        String ip = instance.getHost();
        int socketPort = getSocketPort(instance);
        int httpPort = instance.getPort();
        GameGatewayInfo gameGatewayInfo = new GameGatewayInfo();
        gameGatewayInfo.setId(id);
        gameGatewayInfo.setIp(ip);
        gameGatewayInfo.setSocketPort(socketPort);
        gameGatewayInfo.setHttpPort(httpPort);
        return gameGatewayInfo;
    }

    public static int getSocketPort(ServiceInstance instance) {
        String socketPort = instance.getMetadata().get("socketPort");
        if (socketPort == null) {
            socketPort = "6000";
        }
        return Integer.parseInt(socketPort);
    }

    public static Builder newBuilder(ServiceInstance instance){
        return new Builder(instance);
    }

    public static class Builder{
        private static AtomicInteger gatewaySeed = new AtomicInteger(1);
        private ServiceInstance instance;
        private Builder(ServiceInstance instance){
            this.instance = instance;
        }

        public GameGatewayInfo build(){
            String ip = instance.getHost();
            int socketPort = getSocketPort(instance);
            int httpPort = instance.getPort();
            GameGatewayInfo gameGatewayInfo = new GameGatewayInfo();
            gameGatewayInfo.setId(gatewaySeed.getAndIncrement());
            gameGatewayInfo.setIp(ip);
            gameGatewayInfo.setSocketPort(socketPort);
            gameGatewayInfo.setHttpPort(httpPort);
            return gameGatewayInfo;
        }

    }

    @Override
    public String toString() {
        return "GatewayInfoResponse{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", socketPort=" + socketPort +
                ", httpPort=" + httpPort +
                '}';
    }
}
