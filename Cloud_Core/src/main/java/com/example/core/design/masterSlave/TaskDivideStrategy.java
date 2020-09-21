package com.example.core.design.masterSlave;

/**
 * 对原始任务分解算法策略的抽象
 */
public interface TaskDivideStrategy<T> {
    T nextTask();
}
