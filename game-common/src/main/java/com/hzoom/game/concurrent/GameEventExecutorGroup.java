package com.hzoom.game.concurrent;

import io.netty.util.concurrent.*;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameEventExecutorGroup extends AbstractEventExecutorGroup {

    private final EventExecutor[] children;
    private final AtomicInteger childIndex = new AtomicInteger();
    private final AtomicInteger terminatedChildren = new AtomicInteger();
    private final Promise<?> terminationFuture = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);
    static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", Integer.MAX_VALUE));

    public GameEventExecutorGroup(int nThreads) {
        this(nThreads, null);
    }

    public GameEventExecutorGroup(int nThreads, ThreadFactory threadFactory) {
        this(nThreads, threadFactory, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
    }

    public GameEventExecutorGroup(int nThreads, ThreadFactory threadFactory, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", nThreads));
        }
        if (threadFactory == null) {
            threadFactory = newDefaultThreadFactory();
        }
        children = new SingleThreadEventExecutor[nThreads];
        for (int i = 0; i < nThreads; i++) {
            boolean success = false;
            try {
                children[i] = newChild(threadFactory, maxPendingTasks, rejectedHandler);
                success = true;
            } catch (Exception e) {
                throw new IllegalStateException("failed to create a child event loop", e);
            } finally {
                if (!success) {
                    for (int j = 0; j < i; j++) {
                        children[j].shutdownGracefully();
                    }
                    for (int j = 0; j < i; j++) {
                        EventExecutor child = children[j];
                        try {
                            while (!child.isTerminated()) {
                                child.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }

                    }
                }
            }
        }
        final FutureListener<Object> terminationListener = (future -> {
            if (terminatedChildren.incrementAndGet() == children.length) {
                terminationFuture.setFailure(null);
            }
        });
        for (EventExecutor e : children) {
            e.terminationFuture().addListener(terminationListener);
        }
    }

    private EventExecutor newChild(ThreadFactory threadFactory, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        return new DefaultEventExecutor(this, threadFactory, maxPendingTasks, rejectedHandler);
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(getClass());
    }

    public void execute(Object selectKey, Runnable task) {
        this.select(selectKey).execute(task);
    }

    public <T> Future<T> submit(Object selectKey, Callable<T> task) {
        return this.select(selectKey).submit(task);
    }

    public Future<?> submit(Object selectKey, Runnable task) {
        return this.select(selectKey).submit(task);
    }

    public <T> ScheduledFuture<T> schedule(Object selectKey, Callable<T> task, long delay, TimeUnit unit) {
        return this.select(selectKey).schedule(task, delay, unit);
    }

    public ScheduledFuture<?> schedule(Object selectKey, Runnable task, long delay, TimeUnit unit) {
        return this.select(selectKey).schedule(task, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Object selectKey, Runnable task, long initialDelay, long period, TimeUnit unit) {
        return this.select(selectKey).scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Object selectKey, Runnable task, long initialDelay, long delay, TimeUnit unit) {
        return this.select(selectKey).scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    public EventExecutor select(Object selectKey) {
        if (selectKey == null) {
            throw new IllegalArgumentException("selectKey can't null");
        }
        int hashCode = selectKey.hashCode();
        return this.getEventExecutor(hashCode);
    }

    @Override
    public boolean isShuttingDown() {
        for (EventExecutor e : children) {
            if (!e.isShuttingDown()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        for (EventExecutor e : children) {
            e.shutdownGracefully(quietPeriod, timeout, unit);
        }
        return terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return terminationFuture;
    }

    @Override
    public void shutdown() {
        this.shutdownGracefully();
    }

    @Override
    public boolean isShutdown() {
        for (EventExecutor e : children) {
            if (!e.isShutdown()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        for (EventExecutor e : children) {
            if (!e.isTerminated()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        loop:
        for (EventExecutor e : children) {
            for (; ; ) {
                long timeLeft = deadline - System.nanoTime();
                if (timeLeft <= 0) {
                    break loop;
                }
                if (e.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
                    break;
                }
            }
        }
        return isTerminated();
    }

    @Override
    public EventExecutor next() {
        return this.getEventExecutor(childIndex.getAndIncrement());
    }

    private EventExecutor getEventExecutor(int value) {
        if (isPowerOfTwo(this.children.length)) {
            return children[value & children.length - 1];
        } else {
            return children[Math.abs(value % children.length)];
        }
    }

    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return children().iterator();
    }


    /**
     * java.util.Collections类的newSetFromMap()方法用于返回由指定映射支持的集合。
     * 结果集显示的排序，并发性和性能特征与支持映射相同。
     * 本质上，此工厂方法提供与任何Map实现相对应的Set实现。
     * 不需要在已经具有相应Set实现的Map实现(例如HashMap或TreeMap)上使用此方法。
     */
    protected Set<EventExecutor> children() {
        Set<EventExecutor> children = Collections.newSetFromMap(new LinkedHashMap<EventExecutor, Boolean>());
        Collections.addAll(children, this.children);
        return children;
    }

    /**
     * Return the number of {@link EventExecutor} this implementation uses. This number is the maps 1:1
     * to the threads it use.
     */
    public final int executorCount() {
        return children.length;
    }
}
