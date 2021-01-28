package com.hzoom.game.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.hzoom.game.message.common.MessagePackage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
