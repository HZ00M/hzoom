package com.hzoom.game.dao.base;

import com.hzoom.game.concurrent.GameEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
public abstract class AbstractAsyncDao<T extends AbstractDao> {
    private GameEventExecutorGroup executorGroup;

    private T syncDao;

    public AbstractAsyncDao(T syncDao, GameEventExecutorGroup executorGroup) {
        this.syncDao = syncDao;
        this.executorGroup = executorGroup;
    }

    protected void execute(long playerId, Promise<?> promise, Runnable task) {
        EventExecutor executor = this.executorGroup.select(playerId);
        executor.execute(() -> {
            try {
                task.run();
            } catch (Throwable throwable) {
                log.error("数据库操作失败,playerId:{}", playerId, throwable);
                if (promise != null) {
                    promise.setFailure(throwable);
                }
            }
        });
    }

    protected <V> void execute(long playerId, Promise<V> promise, Callable<V> task) {
        EventExecutor executor = this.executorGroup.select(playerId);
        executor.execute(() -> {
            try {
                V v = task.call();
                promise.setSuccess(v);
            } catch (Throwable throwable) {
                log.error("数据库操作失败,playerId:{}", playerId, throwable);
                if (promise != null) {
                    promise.setFailure(throwable);
                }
            }
        });
    }

    /**
     * 异步更新数据到数据库
     */
    public <ENTITY> Promise<Boolean> saveOrUpdateToDB(long playerId, ENTITY entity, Promise<Boolean> promise) {
        this.execute(playerId, promise, () -> {
            syncDao.saveOrUpdateToDB(entity);
            promise.setSuccess(Boolean.TRUE);
        });
        return promise;
    }

    /**
     * 异步更新数据到redis
     */
    public <ENTITY> Promise<Boolean> saveOrUpdateToRedis(long playerId, ENTITY entity, Promise<Boolean> promise) {
        this.execute(playerId, promise, () -> {
            syncDao.saveOrUpdateToRedis(entity, playerId);
            promise.setSuccess(Boolean.TRUE);
        });
        return promise;
    }

    /**
     * 通过id查找对象
     */
    public <ENTITY> Promise<Optional<ENTITY>> findById(long playerId, Promise<Optional<ENTITY>> promise) {
        this.execute(playerId,promise,()->{
            Optional<ENTITY> entity = syncDao.findById(playerId);
            return entity;
        });
        return promise;
    }
}
