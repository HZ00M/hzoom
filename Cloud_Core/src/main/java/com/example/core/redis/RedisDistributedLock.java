package com.example.core.redis;

import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Order(value = 10)
@Documented
public @interface RedisDistributedLock {
    /**
     * 传入参数的位置数组 0代表第一个参数 1代表第二个参数 参数不存在会抛出异常 一般传入String或者基础类型
     *
     * @return
     */
    int[] lockArgIndexes() default {0};

    /**
     * 传入锁字符串前缀
     *
     * @return
     */
    String lockPreString() default "";


    /**
     * 获取锁等待最长秒数，如果在这个秒数内没有竞争到锁会抛出异常
     *
     * @return
     */
    long waitTime() default 2000;

    /**
     * 获得锁之后最长的执行时间，如果在这个秒数内方法还没执行会锁会被自动释放掉
     *
     * @return
     */
    long leaseTime() default 5000;

    /**
     * 重试次数
     * @return
     */
    int retryNum() default 0;

    /**
     * 重试等待时间
     * @return
     */
    long retryWait() default 500L;
}
