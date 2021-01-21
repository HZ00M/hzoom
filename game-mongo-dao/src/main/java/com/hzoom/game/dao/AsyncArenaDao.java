package com.hzoom.game.dao;

import com.hzoom.game.concurrent.GameEventExecutorGroup;
import com.hzoom.game.dao.base.AbstractAsyncDao;
import com.hzoom.game.dao.base.AbstractDao;

public class AsyncArenaDao extends AbstractAsyncDao {
    public AsyncArenaDao(AbstractDao syncDao, GameEventExecutorGroup executorGroup) {
        super(syncDao, executorGroup);
    }
}
