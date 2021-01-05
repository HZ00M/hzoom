package com.hzoom.game.dao.base;

import com.hzoom.common.concurrent.GameEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public abstract class AbstractAsyncDao {
    private GameEventExecutorGroup executorGroup;

    public AbstractAsyncDao(GameEventExecutorGroup executorGroup){
        this.executorGroup = executorGroup;
    }

    protected void execute(long playerId, Promise<?> promise,Runnable task){
        EventExecutor executor = this.executorGroup.select(playerId);
        executor.execute(()->{
            try {
                task.run();
            }catch (Throwable throwable){
                log.error("数据库操作失败,playerId:{}", playerId, throwable);
                if (promise !=null){
                    promise.setFailure(throwable);
                }
            }
        });
    }

    protected <V> void execute(long playerId, Promise<V> promise, Callable<V> task){
        EventExecutor executor = this.executorGroup.select(playerId);
        executor.execute(()->{
            try {
                V v = task.call();
                promise.setSuccess(v);
            }catch (Throwable throwable){
                log.error("数据库操作失败,playerId:{}", playerId, throwable);
                if (promise !=null){
                    promise.setFailure(throwable);
                }
            }
        });
    }
}
