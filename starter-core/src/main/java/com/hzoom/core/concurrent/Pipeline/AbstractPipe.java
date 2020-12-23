package com.hzoom.core.concurrent.Pipeline;

import java.util.concurrent.TimeUnit;

/**
 * Pipe抽象实现类
 * 该类会调用其子类的doProcess对输入方法进行处理，并将处理结果作为下一个PIpe的输入
 *
 * @param <IN>
 * @param <OUT>
 */
public abstract class AbstractPipe<IN, OUT> implements Pipe<IN, OUT> {
    protected volatile Pipe<?, ?> nextPipe;
    protected volatile PipeContext pipeContext;

    @Override
    public void setNextPipe(Pipe<IN, OUT> nextPipe) {
        this.nextPipe = nextPipe;
    }

    @Override
    public void init(PipeContext context) {
        this.pipeContext = context;
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {

    }

    /**
     * 留给子类实现，用于子类实现任务处理逻辑
     *
     * @param input
     * @throws PipeException
     */
    public abstract OUT doProcess(IN input) throws PipeException;

    @SuppressWarnings("unchecked")
    public void process(IN input) throws InterruptedException {
        try {
            OUT out = doProcess(input);
            if (null != out) {
                ((Pipe<OUT, ?>) nextPipe).process(out);
            }
        } catch (InterruptedException exp1) {
            Thread.currentThread().interrupt();
            ;
        } catch (PipeException exp2) {
            pipeContext.handlerError(exp2);
        }
    }
}
