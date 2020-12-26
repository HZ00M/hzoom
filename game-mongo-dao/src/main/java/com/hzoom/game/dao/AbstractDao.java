package com.hzoom.game.dao;

import com.hzoom.core.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao<ENTITY,ID> {
    private static final String RedisDefaultValue = "#null#";

    @Autowired
    private RedisService redisService;
}
