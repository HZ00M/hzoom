package com.hzoom.core.concurrent.masterSlave;

import com.hzoom.core.concurrent.twoPhaseTermination.AbstractTerminatableThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 基于工作者线程的简单slave通用实现
 *
 * @param <T> 子任务类型
 * @param <V> 子任务处理结果
 */
public abstract class WorkerThreadSlave<T, V> extends AbstractTerminatableThread implements SlaveSpec<T, V> {
    private final BlockingQueue<Runnable> taskQueue;

    public WorkerThreadSlave(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public Future<V> submit(final T task) throws InterruptedException {
        FutureTask<V> futureTask = new FutureTask<V>(new Callable<V>() {
            @Override
            public V call() throws Exception {
                V result;
                try {
                    result = doProcess(task);
                } catch (Exception e) {
                    SubTaskFailureException exp = new SubTaskFailureException(new RetryInfo(task, this::call), e);
                    throw exp;
                }
                return result;
            }
        });
        taskQueue.put(futureTask);
        terminationToken.reservation.incrementAndGet();
        return futureTask;
    }


    @Override
    public void init() {
        start();
    }

    @Override
    public void shutdown() {
        terminate();
    }

    @Override
    protected void doRun() throws Exception {
        try {
            Runnable task = taskQueue.take();
            task.run();
        } finally {
            terminationToken.reservation.decrementAndGet();
        }
    }

    /**
     * 留给子类实现子任务处理逻辑
     *
     * @param task 子任务
     * @return 子任务处理结果
     * @throws InterruptedException
     */
    protected abstract V doProcess(T task) throws InterruptedException;
}
