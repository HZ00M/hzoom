package com.example.core.design.halfSyncHalfAsync;

import java.util.concurrent.*;

/**
 * half-sync/half-async模式可复用实现
 *
 * @param <V> 同步任务的处理结果类型
 */
public abstract class AsyncTask<V> {
    //相当于同步层，用于执行异步层提交的任务
    private volatile Executor executor;
    private final static ExecutorService DEFAULT_EXECUTOR;

    static {
        DEFAULT_EXECUTOR = new ThreadPoolExecutor(1, 1, 8, TimeUnit.HOURS,
                new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "AysncTaskDefaultWorker");
                thread.setDaemon(true);
                return thread;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                /**
                 * 支持重试，当任务呗ThreadPoolExecutor拒绝时
                 * 将重新放入，此时客户端代码需要等待线程池非慢=满
                 */
                if (!executor.isShutdown()) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        ;
                    }
                }

            }
        });
    }

    public AsyncTask(Executor executor) {
        this.executor = executor;
    }

    public AsyncTask() {
        this(DEFAULT_EXECUTOR);
    }

    /**
     * 留给子类实现耗时端的任务，默认实现什么也不做
     *
     * @param params 客户端代码调用dispatch方法时传递的参数列表
     */
    protected void onPreExecute(Object... params) {

    }

    /**
     * 留给子类实现，用于实现同步任务执行后需要执行的操作，默认实现什么也不做
     *
     * @param result
     */
    protected void onPostExecute(V result) {

    }

    protected void onExecutionError(Exception e) {
        e.printStackTrace();
    }

    /**
     * 实现同步任务，由后台线程负责调用
     *
     * @param params 客户端dispatch调用时传递的参数
     */
    protected abstract V doInBackground(Object... params);

    /**
     * 对外暴露的服务方法
     *
     * @param params 客户端传递的参数列表
     */
    protected Future<V> dispatch(final Object... params) {
        FutureTask<V> future = null;
        Callable<V> callable = () -> {
            V result;
            result = doInBackground(params);
            return result;
        };
        future = new FutureTask<V>(callable) {
            @Override
            protected void done() {
                try {
                    onPostExecute(this.get());
                } catch (InterruptedException | ExecutionException e) {
                    onExecutionError(e);
                }
            }
        };
        executor.execute(future);
        return future;
    }
}
