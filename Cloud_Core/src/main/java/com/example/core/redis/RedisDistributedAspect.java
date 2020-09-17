package com.example.core.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Slf4j
public class RedisDistributedAspect {
    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.example.core.redis.RedisDistributedLock)")
    private void pointcut() {
    }

    @Around("pointcut()")
    private Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object[] arguments = point.getArgs();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method targetMethod = methodSignature.getMethod();
        String methodName = targetMethod.getName();
        Class targetClass = point.getTarget().getClass();
        Method realMethod = targetClass.getDeclaredMethod(methodName, targetMethod.getParameterTypes());

        // 获取注解中的参数
        RedisDistributedLock distributedLockMark = realMethod.getAnnotation(RedisDistributedLock.class);
        int[] lockArgIndexes = distributedLockMark.lockArgIndexes();
        String lockPreString = distributedLockMark.lockPreString();
        long waitTime = distributedLockMark.waitTime();
        long leaseTime = distributedLockMark.leaseTime();
        int retryNum = distributedLockMark.retryNum();
        long retryWait = distributedLockMark.retryWait();

        //获取锁对象
        StringBuilder lockBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(lockPreString)) {
            lockBuilder.append(lockPreString);
        } else {
            lockBuilder.append(methodName);
        }
        for (int index : lockArgIndexes) {
            if (index < 0 || index >= arguments.length) {
                throw new DistributedLockException(methodName, "The arg index of " + index + " is not exist please check");
            }
            if (null == arguments[index]) {
                throw new DistributedLockException(methodName, "The arg index of " + index + " can not be null !");
            }
            lockBuilder.append(arguments[index].toString());
        }
        String lockStr = lockBuilder.toString();
        RLock lock = redissonClient.getLock(lockStr.toString());
        Object result = null;
        try {
            boolean tryLock = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (tryLock) {
                result = point.proceed(arguments);
            } else {
                // 如果重试次数为零, 则不重试
                if (retryNum <= 0) {
                    log.info(String.format("Can not get the lock in {%s}", lockStr));
                    throw new DistributedLockException(methodName, "Can not get the lock in " + waitTime + " seconds ; methodName : " +
                            methodName + " args {}" + JSON.toJSONString(arguments));
                }

                if (retryWait == 0) {
                    retryWait = 200L;
                }
                // 设置失败次数计数器, 当到达指定次数时, 返回失败
                int failCount = 1;
                while (failCount <= retryNum) {
                    // 等待指定时间ms
                    Thread.sleep(retryWait);
                    if (lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)) {
                        // 执行主逻辑
                        log.info("start retry lock :{}",lockStr);
                        return point.proceed();
                    } else {
                        log.info("{} had been lock, retrying[{}/{}],retry interval{} milliseconds", lockStr, failCount, retryNum,
                                retryWait);
                        failCount++;
                    }
                }
                throw new DistributedLockException(methodName, "Can not get the lock in " + waitTime + " seconds ; methodName : " +
                        methodName + " args {}" + JSON.toJSONString(arguments));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error in DistributedLock when interrupted thread", e);
        } finally {
            lock.unlock();
            log.info("DistributedLockEnd lockString:{}  ,methodName: {} ,cost time{}", lockStr,
                    methodName, (System.currentTimeMillis() - startTime));
        }
        return result;
    }


}
