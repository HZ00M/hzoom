package com.example.core.design.producerConsumer;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueChannel<T> implements Channel<T> {
    private final BlockingQueue<T> queue;

    public BlockingQueueChannel(BlockingQueue<T> queue){
        this.queue = queue;
    }
    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void put(T t) throws InterruptedException {
        queue.put(t);
    }
}
