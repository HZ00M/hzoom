package com.hzoom.core.concurrent.Pipeline;


import com.hzoom.core.concurrent.twoPhaseTermination.AbstractTerminatableThread;
import com.hzoom.core.concurrent.twoPhaseTermination.TerminationToken;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 基于工作者线程的Pipe实现类   装饰器模式
 * 提交到该pipe的任务由指定个数的工作者线程共同处理
 */
public class WorkerThreadPipeDecorator<IN, OUT> implements Pipe<IN, OUT> {
    protected final BlockingQueue<IN> workQueue;
    private final Set<AbstractTerminatableThread> workerThreads = new HashSet<AbstractTerminatableThread>();
    private final TerminationToken token = new TerminationToken();

    /**
     * 委托者模式
     */
    private final Pipe<IN, OUT> delegate;

    public WorkerThreadPipeDecorator(Pipe<IN, OUT> delegate, int workerCount) {
        this(new SynchronousQueue<IN>(), delegate, workerCount);
    }

    public WorkerThreadPipeDecorator(BlockingQueue<IN> workQueue, Pipe<IN, OUT> delegate, int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount should be positive!");
        }
        this.workQueue = workQueue;
        this.delegate = delegate;
        for (int i = 0; i < workerCount; i++) {
            workerThreads.add(new AbstractTerminatableThread(token) {
                @Override
                protected void doRun() throws Exception {
                    try {
                        dispatch();
                    } finally {
                        token.reservation.decrementAndGet();
                    }
                }
            });
        }
    }

    protected void dispatch() throws InterruptedException {
        IN input = workQueue.take();
        delegate.process(input);
    }


    @Override
    public void setNextPipe(Pipe<IN, OUT> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    @Override
    public void init(PipeContext context) {
        delegate.init(context);
        for (AbstractTerminatableThread thread : workerThreads) {
            thread.start();
        }
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        for (AbstractTerminatableThread thread : workerThreads) {
            thread.terminate();
            try {
                thread.join(TimeUnit.MILLISECONDS.convert(timeout, unit));
            } catch (InterruptedException e) {

            }
        }
        delegate.shutdown(timeout, unit);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        workQueue.put(input);
        token.reservation.incrementAndGet();
    }
}
