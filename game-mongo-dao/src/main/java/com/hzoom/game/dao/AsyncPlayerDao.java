package com.hzoom.game.dao;

import com.hzoom.common.concurrent.GameEventExecutorGroup;
import com.hzoom.game.dao.base.AbstractAsyncDao;
import com.hzoom.game.entity.Player;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class AsyncPlayerDao extends AbstractAsyncDao {
    private PlayerDao playerDao;

    // 由外面注入线程池组，可以使线程池组的共用
    public AsyncPlayerDao(GameEventExecutorGroup executorGroup,PlayerDao playerDao) {
        super(executorGroup);
        this.playerDao = playerDao;
    }

    public Future<Optional<Player>> findPlayer(long playerId, Promise<Optional<Player>> promise) {
        this.execute(playerId,promise,()->{
            Optional<Player> player = playerDao.findById(playerId);
            return player;
        });
        return promise;
    }

    /**
     * 异步更新数据到数据库
     * @param player
     * @param promise
     * @return
     */
    public Promise<Boolean> saveOrUpdatePlayerToDB(Player player,Promise<Boolean> promise) {
        this.execute(player.getPlayerId(),promise,()->{
            playerDao.saveOrUpdateToDB(player);
            promise.setSuccess(Boolean.TRUE);
        });
        return promise;
    }

    /**
     * 异步更新数据到redis
     * @param player
     * @param promise
     * @return
     */
    public Promise<Boolean> saveOrUpdatePlayerToRedis(Player player,Promise<Boolean> promise) {
        this.execute(player.getPlayerId(),promise,()->{
            playerDao.saveOrUpdateToRedis(player,player.getPlayerId());
            promise.setSuccess(Boolean.TRUE);
        });
        return promise;
    }
}
