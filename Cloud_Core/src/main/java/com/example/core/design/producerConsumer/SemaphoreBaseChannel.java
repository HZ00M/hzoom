package com.example.core.design.producerConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class SemaphoreBaseChannel<T> implements Channel<T> {

    private final BlockingQueue<T> queue;
    private final Semaphore semaphore;

    public SemaphoreBaseChannel(BlockingQueue<T> queue, int limitFlow) {
        this.queue = queue;
        this.semaphore = new Semaphore(limitFlow);
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void put(T t) throws InterruptedException {
        semaphore.acquire();
        try {
            queue.put(t);
        } finally {
            semaphore.release();
        }
    }
}
