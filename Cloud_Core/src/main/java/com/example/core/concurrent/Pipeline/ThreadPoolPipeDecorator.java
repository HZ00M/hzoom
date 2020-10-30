package com.example.core.concurrent.Pipeline;


import com.example.core.concurrent.twoPhaseTermination.TerminationToken;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于线程池的Pipe实现类
 */
public class ThreadPoolPipeDecorator<IN, OUT> implements Pipe<IN, OUT> {
    private final Pipe<IN, OUT> delegate;
    private final ExecutorService executorService;

    private final TerminationToken token;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ThreadPoolPipeDecorator(Pipe<IN, OUT> delegate, ExecutorService executorService, TerminationToken token) {
        this.delegate = delegate;
        this.executorService = executorService;
        this.token = token;
    }

    @Override
    public void setNextPipe(Pipe<IN, OUT> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    @Override
    public void init(PipeContext context) {
        delegate.init(context);
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        token.isToShutdown();
        if (token.reservation.get() > 0) {
            try {
                if (countDownLatch.getCount() > 0) {
                    countDownLatch.await(timeout, unit);
                }
            } catch (InterruptedException e) {

            }
        }
        delegate.shutdown(timeout, unit);
    }

    @Override
    public void process(IN input) throws InterruptedException {
        Runnable task = () -> {
            int remainingReservations = -1;
            try {
                delegate.process(input);
            } catch (InterruptedException e) {

            } finally {
                remainingReservations = token.reservation.decrementAndGet();
            }
            if (token.isToShutdown() && 0 == remainingReservations) {
                countDownLatch.countDown();
            }
        };
    }
}
