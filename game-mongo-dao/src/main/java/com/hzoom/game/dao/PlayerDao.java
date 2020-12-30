package com.hzoom.game.dao;

import com.hzoom.game.entity.Player;
import com.hzoom.game.redis.RedisKeyEnum;
import com.hzoom.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerDao extends AbstractDao<Player,Long>{
    @Autowired
    private PlayerRepository repository;

    @Override
    protected RedisKeyEnum getRedisKey() {
        return RedisKeyEnum.PLAYER_INFO;
    }

    @Override
    protected Class<Player> getEntityClass() {
        return Player.class;
    }

    @Override
    protected MongoRepository<Player, Long> getMongoRepository() {
        return repository;
    }
}
