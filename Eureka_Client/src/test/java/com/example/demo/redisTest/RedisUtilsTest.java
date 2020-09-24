package com.example.demo.redisTest;

import com.example.core.redis.RedisUtils;
import com.example.demo.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RedisUtilsTest extends BaseTest{
    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void test(){
        Long hset = redisUtils.hset("key", "field", "123", 200);
        log.info(String.valueOf(hset));
        long key = redisUtils.ttl("key");
        log.info(String.valueOf(key));
    }

}
