package com.hzoom.core.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

public class CounterUnsafe {
    volatile int i = 0 ;
    private static Unsafe unsafe =null;
    private static long valueOffset;
    static {
        try{
          Field field = Unsafe.class.getDeclaredField("theUnsafe");
          field.setAccessible(true);
          unsafe = (Unsafe) field.get(null);
          Field iField = CounterUnsafe.class.getDeclaredField("i");
          valueOffset = unsafe.objectFieldOffset(iField);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }
    public void add(){
        for (;;){
            int current = unsafe.getIntVolatile(this,valueOffset);
            if(unsafe.compareAndSwapInt(this,valueOffset,current,current+1)){
                break;
            }
        }
    }
    public void unsafeAdd(){
        i++;
    }


    public static void main(String[] args){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CounterUnsafe counterUnsafe = new CounterUnsafe();
        RetryLock retryLock = new RetryLock();
        for(int i = 0 ;i<10;i++){
            Thread thread = new Thread(()->{
                try {
                    countDownLatch.await();

                for(int j =0;j<1000;j++){
//                    retryLock.lock();
                    counterUnsafe.add();
//                    counterUnsafe.unsafeAdd();
//                    retryLock.unlock();
                }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        countDownLatch.countDown();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("结果"+counterUnsafe.i);
    }
}
