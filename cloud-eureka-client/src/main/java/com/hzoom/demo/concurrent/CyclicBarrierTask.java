package com.hzoom.demo.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Supplier;


public class CyclicBarrierTask implements Runnable {

    Supplier supplier;

    private CyclicBarrier cyclicBarrier ;

    public CyclicBarrierTask(CyclicBarrier cyclicBarrier, Supplier supplier){
        this.cyclicBarrier = cyclicBarrier;
        this.supplier = supplier;
    }

    @Override
    public void run() {
        try {
            System.out.println("到达集合点");
            cyclicBarrier.await();
            System.out.println("开始执行");
            supplier.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }
}
