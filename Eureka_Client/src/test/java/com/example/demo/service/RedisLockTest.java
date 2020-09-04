package com.example.demo.service;

import com.example.demo.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CountDownLatch;

public class RedisLockTest extends BaseTest {
    @Autowired
    private TestService testService;
    @Autowired
    private JedisPool jedisPool;
    @Test
    public void test() throws InterruptedException {
        String key = "lockkey";
        CountDownLatch countDownLatch = new CountDownLatch(10);
        System.out.println(testService.redisNum);
        Runnable runnable = () -> {
            try {
                countDownLatch.await();
                for (int i=0;i<100;i++){
                    testService.redisNumIncrease(key);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 10; i++) {
            countDownLatch.countDown();
            Thread thread = new Thread(runnable);;
            thread.start();
        }

        Thread.sleep(4000);
        System.out.println(testService.redisNum);
    }

    @Test
    public void test2(){
        Jedis jedis = jedisPool.getResource();
        jedis.set("set","1");
        jedis.setex("test",10,"test");
        String value = jedis.get("test");
        System.out.println(value);
    }
}
