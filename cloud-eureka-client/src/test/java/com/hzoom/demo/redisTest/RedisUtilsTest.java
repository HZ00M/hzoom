package com.hzoom.demo.redisTest;

import com.hzoom.core.redis.RedisService;
import com.hzoom.demo.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RedisUtilsTest extends BaseTest{
    @Autowired
    private RedisService redisUtils;

    @Test
    public void test(){
        Long hset = redisUtils.hset("key", "field", "123", 200);
        log.info(String.valueOf(hset));
        long key = redisUtils.ttl("key");
        log.info(String.valueOf(key));
    }

}
