package com.example.core.concurrent.masterSlave;

import java.util.concurrent.Callable;

/**
 * 失败子任务重试信息
 *
 * @param <T> 子任务类型
 * @param <V> 子任务处理结果类型
 */
public class RetryInfo<T, V> {
    private final T subTask;
    private final Callable<V> redoCommand;

    public RetryInfo(T subTask, Callable<V> redoCommand) {
        this.subTask = subTask;
        this.redoCommand = redoCommand;
    }
}
