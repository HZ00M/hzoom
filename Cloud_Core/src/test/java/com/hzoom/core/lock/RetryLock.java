package com.hzoom.core.lock;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class RetryLock implements Lock {
    //锁拥有者
    AtomicReference<Thread> owner = new AtomicReference<>();
    //等待队列
    private LinkedBlockingDeque<Thread> waiter = new LinkedBlockingDeque<>();


    @Override
    public void lock() {
        if(!tryLock()){
            waiter.offer(Thread.currentThread());
            for (;;){
                Thread head = waiter.peek();
                if(head== Thread.currentThread()){
                    if(!tryLock()){
                    //挂起线程
                        LockSupport.park();
                    }else {
                        waiter.poll();
                        return;
                    }
                }else {
                    LockSupport.park();
                }
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return owner.compareAndSet(null, Thread.currentThread());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock();
    }

    @Override
    public void unlock() {
        if (tryUnlock()){
            Thread th = waiter.peek();
            if(th!=null){
                LockSupport.unpark(th);
            }
        }
    }

    public boolean tryUnlock(){
        //判断当前线程是否占有锁
        if(owner.get()!= Thread.currentThread()){
            throw new IllegalMonitorStateException();
        }else {
            return owner.compareAndSet(Thread.currentThread(),null);
        }

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
