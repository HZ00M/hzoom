package com.hzoom.core.concurrent.producerConsumer;

import java.util.concurrent.BlockingQueue;

public class WorkStealingChannel<T> implements WorkStealingEnabledChannel<T> {
    //受管队列
    private final BlockingQueue<T>[] managedQueues;

    public WorkStealingChannel(BlockingQueue<T>[] managedQueues) {
        this.managedQueues = managedQueues;
    }

    @Override
    public T take(BlockingQueue<T> preferredQueue) throws InterruptedException {
        //优先从指定的队列队首
        BlockingQueue<T> targetQueue = preferredQueue;
        T t = null;
        if (null != targetQueue) {
            t = targetQueue.poll();  //poll队列为空时返回Null,take 队列为空时阻塞
        }
        int queueIndex = -1;
        while (null == t && queueIndex < managedQueues.length) {
            //试图从其他受管队列的队尾取元素
            //窃取算法
            queueIndex = (queueIndex + 1) % managedQueues.length;
            targetQueue = managedQueues[queueIndex];
            if ((targetQueue == preferredQueue)) {
                continue;
            }
            t = targetQueue.poll();
        }
        return t;
    }

    @Override
    public T take() throws InterruptedException {
        return take(null);
    }

    @Override
    public void put(T t) throws InterruptedException {
        int targetIndex = (t.hashCode() % managedQueues.length);
        BlockingQueue<T> targetQueue = managedQueues[targetIndex];
        targetQueue.put(t);
    }
}
