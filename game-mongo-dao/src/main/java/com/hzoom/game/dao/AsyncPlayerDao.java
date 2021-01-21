package com.hzoom.game.dao;

import com.hzoom.game.concurrent.GameEventExecutorGroup;
import com.hzoom.game.dao.base.AbstractAsyncDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncPlayerDao extends AbstractAsyncDao {
    // 由外面注入线程池组，可以使线程池组的共用
    public AsyncPlayerDao(GameEventExecutorGroup executorGroup,PlayerDao playerDao) {
        super(playerDao,executorGroup);
    }

}
