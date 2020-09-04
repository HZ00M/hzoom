package com.example.demo.service;

import com.example.core.redisLock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {
    public int redisNum = 0;

//    @RedisDistributedLock(lockPreString = "test")
    public void redisNumIncrease(String key){
        redisNum++;
    }
}
