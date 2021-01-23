package com.hzoom.game.cloud;

import com.hzoom.core.stream.TopicService;
import com.hzoom.game.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
public class ServiceInstanceManager {
    @Autowired
    private DiscoveryClient discoveryClient;

    private Map<Integer, List<ServerInfo>> serverInfos; // serviceId对应的服务器集合，一个服务可能部署到多台服务器上面，实现负载均衡

    /**
     * 根据玩家id挑选一台服务器
     *
     * @param serviceId
     * @param playerId
     * @return
     */
    public ServerInfo selectServerInfo(Integer serviceId, Long playerId) {
        // 再次声明一下，防止游戏网关列表发生变化，导致数据不一致。
        Map<Integer, List<ServerInfo>> serverInfoMap = serverInfos;
        List<ServerInfo> serverList = serverInfoMap.get(serviceId);
        if (serverList == null || serverList.isEmpty()) {
            return null;
        }
        int hashCode = Math.abs(playerId.hashCode());
        int serverCount = serverList.size();
        int index = hashCode % serverCount;
        if (index >= serverCount) {
            index = serverCount - 1;
        }
        return serverList.get(index);
    }

    /**
     * 判断某个服务中的serverId是否还有效
     *
     * @param serviceId
     * @param serverId
     * @return
     */
    public boolean isEnableServer(Integer serviceId, Integer serverId) {
        // 再次声明一下，防止游戏网关列表发生变化，导致数据不一致。
        Map<Integer, List<ServerInfo>> serverInfoMap = serverInfos;
        List<ServerInfo> serverList = serverInfoMap.get(serviceId);
        if (serverList != null) {
            return serverList.stream().anyMatch(serverInfo -> serverInfo.getServerId() == serverId);
        }
        return false;
    }

    public Set<Integer> getAllServiceId() {
        return serverInfos.keySet();
    }

    @EventListener(HeartbeatEvent.class)
    public void listener(HeartbeatEvent event) {
        refreshServiceInfo();
    }

    @PostConstruct
    public void init() {
        refreshServiceInfo();
    }

    private void refreshServiceInfo() {
        Map<Integer, List<ServerInfo>> tempServerInfoMap = new HashMap<>();
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("game-logic");//网取网关后面的服务实例
        log.debug("抓取游戏服务配置成功,{}", serviceInstances);
        serviceInstances.forEach(instance -> {
            int weight = getInstanceWeight(instance);
            for (int i = 0; i < weight; i++) {
                ServerInfo serverInfo = ServerInfo.newServerInfo(instance);
                List<ServerInfo> serverList = tempServerInfoMap.get(serverInfo.getServiceId());
                if (serverList == null) {
                    serverList = new ArrayList<>();
                    tempServerInfoMap.put(serverInfo.getServiceId(), serverList);
                }
                serverList.add(serverInfo);
            }
        });
        this.serverInfos = tempServerInfoMap;
    }

    private int getInstanceWeight(ServiceInstance instance) {
        String weight = instance.getMetadata().get("weight");
        if (StringUtils.isEmpty(weight)) {
            weight = "1";
        }
        return Integer.parseInt(weight);
    }


}
