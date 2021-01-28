package com.hzoom.game.cloud;

import com.hzoom.core.redis.RedisService;
import com.hzoom.game.error.GatewaySocketError;
import com.hzoom.game.event.GameChannelCloseEvent;
import com.hzoom.game.exception.ErrorException;
import com.hzoom.game.model.ServerInfo;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务实例管理器
 */
@Component
@Slf4j
public class PlayerServiceInstanceManager {
    @Autowired(required = false)
    private RedisService redisService;
    @Autowired
    private ServiceInstanceManager instanceService;
    /**
     * 缓存PlayerID对应的所有的服务的实例的id,最外层的key是playerId，里面的Map的key是serviceId，value是serverId
     */
    private Map<Long, Map<Integer, Integer>> serviceInstanceMap = new ConcurrentHashMap<>();
    /**
     * 创建一个事件线程，操作redis的时候，使用异步
     */
    private EventExecutor eventExecutor = new DefaultEventExecutor();

    public Promise<Integer> selectServerId(Long playerId, Integer serviceId, Promise<Integer> promise) {
        Map<Integer, Integer> instanceMap = serviceInstanceMap.get(playerId);
        Integer serverId = null;
        if (instanceMap != null) {//缓存中存在直接从缓存中获取
            serverId = instanceMap.get(serviceId);
        } else {
            instanceMap = new ConcurrentHashMap<>();
            serviceInstanceMap.put(playerId, instanceMap);
        }
        if (serverId != null) {//检测这个serverId是否有效,并重新获取
            boolean enable = instanceService.isEnableServer(serviceId, serverId);
            if (enable) {
                return promise.setSuccess(serverId);
            } else {
                serverId = null;
            }
        }
        if (serverId == null) {
            eventExecutor.execute(() -> {
                try {
                    String key = getRedisKey(playerId);// 从redis查找一下，是否已由别的服务计算好
                    Integer result = redisService.hget(key, String.valueOf(serviceId), Integer.class);
                    boolean resultEnable = true;
                    if (result != null) {
                        resultEnable = instanceService.isEnableServer(serviceId, result);
                        if (resultEnable) {// 如果redis中已缓存且是有效的服务实例serverId，直接返回
                            promise.setSuccess(result);
                            addLocalCache(playerId, serviceId, result);
                        }
                    }
                    if (result == null) {// 如果Redis中没有缓存，或实例已失效，重新获取一个新的服务实例Id
                        result = selectServerIdAndSaveRedis(serviceId, playerId);
                        if (result == null) {
                            throw ErrorException.newBuilder(GatewaySocketError.NOT_INSTANCE).build();
                        }
                        addLocalCache(playerId, serviceId, result);
                        promise.setSuccess(result);
                    }
                } catch (Throwable e) {
                    promise.setFailure(e);
                    log.error("selectServerId fail ! message:{}", e.getMessage());
                }

            });
        }
        return promise;
    }

    public Set<Integer> getAllServiceId() {
        return instanceService.getAllServiceId();
    }

    @EventListener(GameChannelCloseEvent.class)
    public void remove(GameChannelCloseEvent event) {
        serviceInstanceMap.remove(event.getPlayerId());
    }

    private Integer selectServerIdAndSaveRedis(Integer serviceId, Long playerId) {
        ServerInfo serverInfo = instanceService.selectServerInfo(serviceId, playerId);
        if (serverInfo != null) {
            Integer serverId = serverInfo.getServerId();
            eventExecutor.execute(() -> {
                try {
                    String key = getRedisKey(playerId);
                    redisService.hset(key, String.valueOf(serviceId), String.valueOf(serverId));
                } catch (Exception e) {
                    log.error("selectServerIdAndSaveRedis fail ! message:{}", e.getMessage());
                }
            });
        }
        return null;
    }

    private void addLocalCache(Long playerId, Integer serviceId, Integer serverId) {
        Map<Integer, Integer> instanceMap = serviceInstanceMap.get(playerId);
        instanceMap.put(serviceId, serverId);
    }

    private String getRedisKey(Long playerId) {
        return "service_instance_" + playerId;
    }
}
