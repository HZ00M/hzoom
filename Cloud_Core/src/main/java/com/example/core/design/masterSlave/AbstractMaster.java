package com.example.core.design.masterSlave;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * master参与者的可复用实现
 * @param <T> 子任务对象类型
 * @param <V> 子任务的处理结果类型
 * @param <R> 原始任务处理结果类型
 */
public abstract class AbstractMaster<T,V,R> {
    protected volatile Set<? extends SlaveSpec<T,V>> slaves;
    private volatile SubTaskDispatchStrategy<T,V> dispatchStrategy;

    public AbstractMaster(){}

    protected void init(){
        slaves = createSlaves();
        dispatchStrategy = newSubTaskDispatchStrategy();
        for (SlaveSpec<T,V> slave:slaves){
            slave.init();
        }
    }

    /**
     * 创建子任务分解算法
     * @param params
     * @return
     */
    protected abstract TaskDivideStrategy<T> newTaskDivideStrategy(Object... params);

    protected SubTaskDispatchStrategy<T,V> newSubTaskDispatchStrategy(){
        return new RoundRobinSubTaskDispatchStrategy<T,V>();
    }

    /**
     * 创建s一组lave参与者
     * @return
     */
    protected abstract Set<? extends SlaveSpec<T,V>> createSlaves();

    /**
     * 合并子任务处理结果
     * @param subResults
     * @return
     */
    protected abstract R combineResult(Iterator<Future<V>> subResults);

    /**
     * 对子类暴露的服务方法
     * 该方法使用了模板模式，策略模式
     * @param params
     * @return
     * @throws Exception
     */
    protected R service(Object... params)throws Exception {
        final TaskDivideStrategy<T> taskDivideStrategy = newTaskDivideStrategy(params);
        /**
         * 对原始任务进行分解，并将分解得来的任务派发给slave参与者实例,这里使用了策略模式
         */
        Iterator<Future<V>> subResults = dispatchStrategy.dispatch(slaves,taskDivideStrategy);
        //等待slave实例处理结束
        for (SlaveSpec<T,V> slave:slaves){
            slave.shutdown();
        }
        R result = combineResult(subResults);
        return result;
    }
}
