package com.hzoom.game.channel;

import com.hzoom.game.dao.AsyncArenaDao;
import com.hzoom.game.entity.Arena;
import com.hzoom.game.entity.manager.ArenaManager;
import com.hzoom.message.channel.AbstractGameChannelHandlerContext;
import com.hzoom.message.channel.GameChannelPromise;
import com.hzoom.message.handler.AbstractGameMessageDispatchHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Slf4j
public class ArenaGatewayHandler extends AbstractGameMessageDispatchHandler<ArenaManager> {
    private ArenaManager arenaManager;
    private AsyncArenaDao asyncArenaDao;

    public ArenaGatewayHandler(ApplicationContext applicationContext) {
        super(applicationContext);
        this.asyncArenaDao = applicationContext.getBean(AsyncArenaDao.class);
    }

    @Override
    protected ArenaManager getDataManager() {
        return arenaManager;
    }

    @Override
    protected void initData(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise initPromise) {
        // 异步加载竞技场信息
        Promise<Optional<Arena>> arenaPromise = new DefaultPromise<>(ctx.executor());
        asyncArenaDao.findById(playerId,arenaPromise).addListener((GenericFutureListener<Future<Optional<Arena>>>) f -> {
            if (f.isSuccess()) {
                Optional<Arena> optionalArena = f.get();
                if (optionalArena.isPresent()) {
                    arenaManager = new ArenaManager(optionalArena.get());
                } else {
                    Arena arena = new Arena();
                    arena.setPlayerId(playerId);
                    arenaManager = new ArenaManager(arena);
                }
                initPromise.setSuccess();
            } else {
                log.error("查询竞技场信息失败", f.cause());
                initPromise.setFailure(f.cause());
            }
        });
    }

    @Override
    protected Future<Boolean> updateToRedis(Promise<Boolean> promise) {
        asyncArenaDao.saveOrUpdateToRedis(playerId,arenaManager.getArena(),promise);
        return promise;
    }

    @Override
    protected Future<Boolean> updateToDB(Promise<Boolean> promise) {
        asyncArenaDao.saveOrUpdateToDB(playerId,arenaManager.getArena(),promise);
        return promise;
    }
}
