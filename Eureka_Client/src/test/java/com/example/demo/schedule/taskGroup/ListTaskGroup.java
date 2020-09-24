package com.example.demo.schedule.taskGroup;


import com.example.demo.schedule.exception.QueryException;
import com.example.demo.schedule.task.OrderTask;

import java.util.*;

/**
 * @author Ziming
 *  基于List的任务组实现
 *      支持场景：顺序执行
 *
 */
public class ListTaskGroup<R> implements TaskGroup {

    private LinkedList<OrderTask> tasks;
    private String taskGourpName = "ListTaskGroup#" + this.hashCode();
    private boolean isDone = false;
    private boolean isSuccess = false;
    private R result ;

    public ListTaskGroup(String taskGourpName) {
        this();
        this.taskGourpName = taskGourpName;
    }

    public ListTaskGroup() {
        tasks = new LinkedList<>();
    }

    @Override
    public R call() throws Exception {
        work();
        return result;
    }

    @Override
    public void work() {
        sortTask();
        Iterator<OrderTask> iterator = tasks.iterator();
        try {
            while (iterator.hasNext()) {
                result = (R) iterator.next().execute(result);
            }
            workDone();
        } catch (QueryException e) {
            //log
            workFailure(e);
        }

    }

    private void workDone() {
        isSuccess = true;
        isDone = true;
    }

    private void workFailure(QueryException e) {
        isSuccess = false;
        isDone = true;
        result = (R) Optional.of(e);
    }

    @Override
    public boolean add(OrderTask task) {
        return tasks.add(task);
    }

    @Override
    public boolean add(Collection<OrderTask> collection) {
        return tasks.addAll(collection);
    }

    @Override
    public boolean remove(OrderTask task) {
        return tasks.remove(task);
    }

    @Override
    public boolean removeAll() {
        return tasks.removeAll(tasks);
    }


    public void sortTask() {
        tasks.sort(Comparator.comparing(OrderTask::getOrder));
    }

    public String getTaskGourpName() {
        return taskGourpName;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public int size() {
        return tasks.size();
    }

}
