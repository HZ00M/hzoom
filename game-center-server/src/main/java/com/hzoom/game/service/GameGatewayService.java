package com.hzoom.game.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hzoom.game.error.GameCenterError;
import com.hzoom.game.exception.ErrorException;
import com.hzoom.game.model.GameGatewayInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 负责对网关进行管理，主要功能是网关配置变化更新，网关分配。网关存活检测。
 */
@Service
@Slf4j
public class GameGatewayService {
    @Autowired
    private DiscoveryClient discoveryClient;
    private List<GameGatewayInfo> gameGameGatewayInfoList; // 参与网关分配的网关集合
    private LoadingCache<Long, GameGatewayInfo> userGameGatewayCache;// 用户分配到的网关缓存

    @PostConstruct
    public void init() {
        refreshGatewayInfo();
        userGameGatewayCache = CacheBuilder.newBuilder().maximumSize(20000).expireAfterAccess(Duration.ofHours(2)).build(new CacheLoader<Long, GameGatewayInfo>() {
            @Override
            public GameGatewayInfo load(Long playerId) throws Exception {
                GameGatewayInfo gameGatewayInfo = selectGameGatewayInfoByPlayerId(playerId);
                return gameGatewayInfo;
            }
        });
    }


    private void refreshGatewayInfo() {
        List<ServiceInstance> gameGatewayServiceInstances = discoveryClient.getInstances("game-gateway");
        log.info("GameGatewayService 抓取游戏网管配置成功 {}", gameGatewayServiceInstances);
        List<GameGatewayInfo> initGameGatewayInfos = new ArrayList<>();
        for (ServiceInstance instance : gameGatewayServiceInstances) {
            int weight = getInstanceWeight(instance);
            for (int i = 0; i < weight; i++) {
                GameGatewayInfo gameGatewayInfo = GameGatewayInfo.newBuilder(instance).build();
                initGameGatewayInfos.add(gameGatewayInfo);
            }
        }
        Collections.shuffle(initGameGatewayInfos);
        this.gameGameGatewayInfoList = initGameGatewayInfos;
    }


    private int getInstanceWeight(ServiceInstance instance) {
        String weight = instance.getMetadata().get("weight");
        if (weight == null) {
            weight = "0";
        }
        return Integer.parseInt(weight);
    }

    public GameGatewayInfo selectGameGatewayInfoByPlayerId(Long playerId) {
        // 再次声明一下，防止游戏网关列表发生变化，导致数据不一致。
        List<GameGatewayInfo> tempGameGatewayInfoList = gameGameGatewayInfoList;
        if (CollectionUtils.isEmpty(tempGameGatewayInfoList)) {
            throw ErrorException.newBuilder(GameCenterError.NO_GAME_GATEWAY_INFO).build();
        }
        int hashCode = Math.abs(playerId.hashCode());
        int gatewayCount = tempGameGatewayInfoList.size();
        int index = hashCode % gatewayCount;
        return tempGameGatewayInfoList.get(index);
    }

}
