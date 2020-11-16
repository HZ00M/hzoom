package com.hzoom.core.concurrent.masterSlave;

import java.util.concurrent.Future;

/**
 * slave参与者
 *
 * @param <T> 子任务类型
 * @param <V> 子任务结果类型
 */
public interface SlaveSpec<T, V> {
    /**
     * @param task 用于master向其提交子任务
     * @return 可借以获取子任务的处理结果promise
     * @throws InterruptedException
     */
    Future<V> submit(final T task) throws InterruptedException;

    /**
     * 初始化提供服务
     */
    void init();

    /**
     * 停止slave实例对外提供服务
     */
    void shutdown();
}
