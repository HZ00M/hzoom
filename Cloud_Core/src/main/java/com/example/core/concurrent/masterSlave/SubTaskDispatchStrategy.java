package com.example.core.concurrent.masterSlave;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * '
 * 对子对象派发算法策略的对象
 *
 * @param <T> 子任务类型
 * @param <V> 子类型处理结果返回值
 */
public interface SubTaskDispatchStrategy<T, V> {
    /**
     * 根据指定的原始任务分解策略，将分解的子任务派发给一组slave参与者实例
     *
     * @param slaves              slave实例
     * @param tTaskDivideStrategy 原始任务分解策略
     * @return 遍历该Iterator可获取子任务处理结果的promise
     * @throws InterruptedException
     */
    Iterator<Future<V>> dispatch(Set<? extends SlaveSpec<T, V>> slaves, TaskDivideStrategy<T> tTaskDivideStrategy) throws InterruptedException;
}
