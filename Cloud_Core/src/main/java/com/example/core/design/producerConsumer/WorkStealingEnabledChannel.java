package com.example.core.design.producerConsumer;

import java.util.concurrent.BlockingQueue;

public interface WorkStealingEnabledChannel<T> extends Channel<T> {
    T take(BlockingQueue<T> preferredQueue) throws InterruptedException;
}
