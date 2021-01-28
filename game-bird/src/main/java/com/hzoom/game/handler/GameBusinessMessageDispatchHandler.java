package com.hzoom.game.handler;

import com.hzoom.game.dao.AsyncPlayerDao;
import com.hzoom.game.entity.Player;
import com.hzoom.game.entity.manager.PlayerManager;
import com.hzoom.game.message.DispatchMessageManager;
import com.hzoom.message.channel.AbstractGameChannelHandlerContext;
import com.hzoom.message.channel.GameChannelPromise;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.message.context.DispatchUserEventManager;
import com.hzoom.message.handler.AbstractGameMessageDispatchHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Slf4j
public class GameBusinessMessageDispatchHandler extends AbstractGameMessageDispatchHandler<PlayerManager> {

    private Player player;
    private PlayerManager playerManager;
    private AsyncPlayerDao playerDao;
    public GameBusinessMessageDispatchHandler(ApplicationContext applicationContext, ChannelServerProperties channelServerProperties, DispatchMessageManager dispatchMessageManager, DispatchUserEventManager userEventManager, AsyncPlayerDao playerDao) {
        super(applicationContext);
        this.playerDao = playerDao;
    }

    @Override
    protected PlayerManager getDataManager() {
        return playerManager;
    }

    @Override
    protected Future<Boolean> updateToRedis(Promise<Boolean> promise) {
        return playerDao.saveOrUpdateToRedis(playerId,player,promise);
    }

    @Override
    protected Future<Boolean> updateToDB(Promise<Boolean> promise) {
        return playerDao.saveOrUpdateToDB(playerId,player, promise);
    }

    @Override
    protected void initData(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise) {
        playerDao.findById(playerId, new DefaultPromise<>(ctx.executor())).addListener(new GenericFutureListener<Future<Optional<Player>>>() {
            @Override
            public void operationComplete(Future<Optional<Player>> future) throws Exception {
                Optional<Player> playerOp = future.get();
                if (playerOp.isPresent()) {
                    player = playerOp.get();
                    playerManager = new PlayerManager(player);
                    promise.setSuccess();
                } else {
                    log.error("player {} 不存在", playerId);
                    promise.setFailure(new IllegalArgumentException("找不到Player数据，playerId:" + playerId));
                }
            }
        });
    }
}
