package com.hzoom.game.model;

import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.StringUtils;

@Data
public class ServerInfo {
    private int serviceId; //服务id，与MessageMetadata中的一致
    private int serverId;  //服务器id
    private String host;
    private int port;

    public static ServerInfo newServerInfo(ServiceInstance instance) {
        String serviceId = instance.getMetadata().get("serviceId");
        String serverId =  instance.getMetadata().get("serverId");
        if (StringUtils.isEmpty(serviceId)) {
            throw new IllegalArgumentException(instance.getHost() + "的服务未配置serviceId");
        }

        if (StringUtils.isEmpty(serverId)) {
            throw new IllegalArgumentException(instance.getHost() + "的服务未配置serverId");
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServiceId(Integer.parseInt(serviceId));
        serverInfo.setServerId(Integer.parseInt(serverId));
        serverInfo.setHost(instance.getHost());
        serverInfo.setPort(instance.getPort());

        return serverInfo;
    }
}
