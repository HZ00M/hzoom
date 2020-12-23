package com.hzoom.core.concurrent.callbackTask;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CallbackTaskScheduler extends Thread {
    private ConcurrentLinkedQueue<CallbackTask> executeTaskQueue = new ConcurrentLinkedQueue<>();

    private ExecutorService jPool = Executors.newCachedThreadPool();

    ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPool);

    private long sleepTime = 200;// 线程休眠时间

    private static class SingletonHolder {
        private static final CallbackTaskScheduler INSTANCE = new CallbackTaskScheduler();
    }

    public static final CallbackTaskScheduler getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private CallbackTaskScheduler() {
        this.start();
    }

    public static <T> void add(CallbackTask<T> callbackTask){
        getInstance().executeTaskQueue.add(callbackTask);
    }

    @Override
    public void run() {
        while (true) {
            handleTask();// 处理任务
            threadSleep(sleepTime);
        }
    }

    /**
     * 处理任务队列，检查其中是否有任务
     */
    private void handleTask() {

        CallbackTask executeTask = null;
        while (executeTaskQueue.peek() != null) {
            executeTask = executeTaskQueue.poll();
            handleTask(executeTask);
        }
    }

    /**
     * 执行任务操作
     */
    private <R> void handleTask(CallbackTask<R> executeTask) {
        ListenableFuture<R> future = gPool.submit(executeTask::execute);

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }
            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });

    }

    private void threadSleep(long time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            log.error("线程等待异常:",e);
        }
    }
}
