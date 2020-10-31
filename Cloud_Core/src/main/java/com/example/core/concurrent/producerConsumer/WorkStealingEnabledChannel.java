package com.example.core.concurrent.producerConsumer;

import java.util.concurrent.BlockingQueue;

public interface WorkStealingEnabledChannel<T> extends Channel<T> {
    T take(BlockingQueue<T> preferredQueue) throws InterruptedException;
}
