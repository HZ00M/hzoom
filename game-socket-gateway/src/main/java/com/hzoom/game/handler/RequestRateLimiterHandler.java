package com.hzoom.game.handler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.server.GatewayServerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
@Component
public class RequestRateLimiterHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private GatewayServerProperties gatewayServerProperties;
    private RateLimiter globalRateLimiter = RateLimiter.create(20); // 全局限制器
    private LoadingCache<String, RateLimiter> userRateLimiterCache = CacheBuilder.newBuilder()
            .maximumSize(2000).expireAfterAccess(1000 * 60, TimeUnit.MILLISECONDS)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) throws Exception {
                    // 不存在限流器就创建一个。
                    double permitsPerSecond = 20;
                    RateLimiter newRateLimiter = RateLimiter.create(permitsPerSecond);
                    return newRateLimiter;
                }
            });
    private Map<Long, Integer> clientSeqMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage messagePackage = (MessagePackage) msg;
        int clientSeqId = messagePackage.getHeader().getClientSeqId();
        long playerId = messagePackage.getHeader().getPlayerId();
        RateLimiter userRateLimiter = userRateLimiterCache.get(String.valueOf(playerId));
        if (!userRateLimiter.tryAcquire()) {
            log.info("用户{}请求过多，触发限流!", playerId);
            ctx.close();
            return;
        }
        if (!globalRateLimiter.tryAcquire()) {
            log.info("全局请求过多，用户{}触发限流!", playerId);
            ctx.close();
            return;
        }
        Integer lastClientSeqId = clientSeqMap.get(playerId);
        if (lastClientSeqId != null && clientSeqId <= lastClientSeqId) {
            return;
        }
        clientSeqMap.put(playerId, clientSeqId);
        ctx.fireChannelRead(msg);

    }

}
