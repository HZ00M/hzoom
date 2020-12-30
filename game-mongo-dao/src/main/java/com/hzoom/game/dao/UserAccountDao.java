package com.hzoom.game.dao;

import com.hzoom.game.entity.UserAccount;
import com.hzoom.game.redis.RedisKeyEnum;
import com.hzoom.game.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

public class UserAccountDao extends AbstractDao<UserAccount,String>{
    @Autowired
    private UserAccountRepository repository;

    public long getNextUserId() {
        String key = RedisKeyEnum.USER_ID_INCR.getKey();
        long userId = redisService.incr(key);
        return userId;
    }

    @Override
    protected RedisKeyEnum getRedisKey() {
        return RedisKeyEnum.USER_INFO;
    }

    @Override
    protected Class<UserAccount> getEntityClass() {
        return UserAccount.class;
    }

    @Override
    protected MongoRepository<UserAccount, String> getMongoRepository() {
        return repository;
    }
}
