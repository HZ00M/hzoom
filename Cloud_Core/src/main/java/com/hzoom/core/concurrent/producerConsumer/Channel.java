package com.hzoom.core.concurrent.producerConsumer;

/**
 * 角色模式 channel
 * 对通道参与者进行抽象
 */
public interface Channel<T> {
    T take() throws InterruptedException;

    void put(T t) throws InterruptedException;
}
