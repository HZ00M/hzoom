package com.hzoom.game.dao;

import com.hzoom.game.dao.base.AbstractDao;
import com.hzoom.game.entity.Arena;
import com.hzoom.game.redis.RedisKeyEnum;
import com.hzoom.game.repository.ArenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class ArenaDao extends AbstractDao<Arena,Long> {
    @Autowired
    private ArenaRepository arenaRepository;

    @Override
    protected RedisKeyEnum getRedisKey() {
        return RedisKeyEnum.ARENA;
    }

    @Override
    protected Class<Arena> getEntityClass() {
        return Arena.class;
    }

    @Override
    protected MongoRepository<Arena, Long> getMongoRepository() {
        return arenaRepository;
    }
}
