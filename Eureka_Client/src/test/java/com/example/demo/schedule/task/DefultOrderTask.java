package com.example.demo.schedule.task;

import com.example.demo.schedule.exception.QueryException;

import java.util.function.Function;

/**
 * @author Ziming
 *  默认执行任务实现
 * @param <IN> 上一个任务执行结果
 * @param <OUT> 当前任务执行结果
 */
public  class DefultOrderTask<IN, OUT> implements OrderTask<IN, OUT> {
    private Function<IN, OUT> task;
    private int order;

    public DefultOrderTask(Function task) {
        this.task = task;
    }

    public DefultOrderTask(Function task, int order) {
        this.task = task;
        this.order = order;
    }

    @Override
    public OUT execute(IN t) throws QueryException {
        try {
            return task.apply(t);
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e.getCause(), task);
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
