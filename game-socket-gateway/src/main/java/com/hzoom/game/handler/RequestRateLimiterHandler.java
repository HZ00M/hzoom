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
public class RequestRateLimiterHandler extends ChannelInboundHandlerAdapter {
    private RateLimiter globalRateLimiter; // 全局限制器
    private static RateLimiter userRateLimiter;
    private Map<Long, Integer> clientSeqMap = new ConcurrentHashMap<>();

    public RequestRateLimiterHandler(RateLimiter globalRateLimiter, double requestPerSecond) {
        this.globalRateLimiter = globalRateLimiter;
        userRateLimiter = RateLimiter.create(requestPerSecond);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePackage messagePackage = (MessagePackage) msg;
        int clientSeqId = messagePackage.getHeader().getClientSeqId();
        long playerId = messagePackage.getHeader().getPlayerId();
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
        lastClientSeqId =clientSeqId;
        clientSeqMap.put(playerId, lastClientSeqId);
        ctx.fireChannelRead(msg);

    }

}
