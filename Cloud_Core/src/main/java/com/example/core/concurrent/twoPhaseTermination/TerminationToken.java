package com.example.core.concurrent.twoPhaseTermination;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken {
    boolean toShutdown = false;
    public final AtomicInteger reservation = new AtomicInteger();

    /**
     * 在多个可停止线程实例共享一个TerminationToken实例的情况下，该队列用于记录这些共享
     * 尽可能减少锁的情况下，实现这些线程实例的停止
     */
    private final Queue<WeakReference<Terminatable>> coordinatedThreads;

    public TerminationToken() {
        coordinatedThreads = new ConcurrentLinkedDeque<WeakReference<Terminatable>>();
    }

    public boolean isToShutdown() {
        return toShutdown;
    }

    protected void setToShutdown(boolean toShutdown) {
        this.toShutdown = toShutdown;
    }

    protected void register(Terminatable thread) {
        coordinatedThreads.add(new WeakReference<Terminatable>(thread));
    }

    /**
     * 通知TerminationToken实例，共享该实例的所有可停止线程中的一个线程终止了
     */
    protected void notifyThreadTermination(Terminatable thread) {
        WeakReference<Terminatable> weThread;
        Terminatable otherThread;
        while (null != (weThread = coordinatedThreads.poll())) {
            otherThread = weThread.get();
            if (null != otherThread && otherThread != thread) {
                otherThread.terminate();
            }
        }
    }

}
