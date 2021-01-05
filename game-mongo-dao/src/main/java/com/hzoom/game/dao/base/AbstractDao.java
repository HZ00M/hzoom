package com.hzoom.game.dao.base;

import com.alibaba.fastjson.JSON;
import com.hzoom.core.redis.RedisService;
import com.hzoom.game.redis.RedisKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Duration;
import java.util.Optional;

public abstract class AbstractDao<ENTITY, ID> {
    private static final String REDIS_DEFAULT_VALUE = "#null#";
    @Autowired
    protected RedisService redisService;

    protected abstract RedisKeyEnum getRedisKey();

    protected abstract Class<ENTITY> getEntityClass();

    protected abstract MongoRepository<ENTITY, ID> getMongoRepository();

    public Optional<ENTITY> findById(ID id) {
        String key = this.getRedisKey().getKey(id.toString());
        String value = redisService.get(key);
        ENTITY entity = null;
        if (null == value) {
            key = key.intern();//保证字符串在常量池中
            synchronized (key) {// 这里对openId加锁，防止并发操作，导致缓存击穿。
                value = redisService.get(key);// 这里二次获取一下
                if (null == value) {
                    Optional<ENTITY> op = this.getMongoRepository().findById(id);
                    if (op.isPresent()) {
                        entity = op.get();
                        this.updateRedis(entity, id);
                    } else {
                        this.setRedisDefaultValue(key);
                    }
                }
            }
        } else if (value.equals(REDIS_DEFAULT_VALUE)) {
            entity = null;
        }
        if (value != null) {
            entity = JSON.parseObject(value, this.getEntityClass());
        }
        return Optional.ofNullable(entity);
    }

    private void updateRedis(ENTITY entity, ID id) {
        String key = this.getRedisKey().getKey(id.toString());
        String value = JSON.toJSONString(entity);
        redisService.setex(key, this.getRedisKey().getTimeOutSecond(), value);
    }

    private void setRedisDefaultValue(String key) {
        Duration duration = Duration.ofMinutes(1);
        redisService.setex(key, (int) duration.getSeconds(), REDIS_DEFAULT_VALUE);
    }

    public void saveOrUpdateToDB(ENTITY entity) {
        this.getMongoRepository().save(entity);
    }

    public void saveOrUpdateToRedis(ENTITY entity, ID id) {
        this.updateRedis(entity, id);
    }
}
