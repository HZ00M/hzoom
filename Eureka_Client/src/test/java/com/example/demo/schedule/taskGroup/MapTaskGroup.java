package com.example.demo.schedule.taskGroup;

import com.example.demo.schedule.exception.QueryException;
import com.example.demo.schedule.task.OrderTask;

import java.util.*;

/**
 * @author Ziming
 *  基于Map的任务组实现
 *      支持场景：需要进行fork/join的任务
 *
 */
public class MapTaskGroup<R> implements TaskGroup {
    private TreeMap<Integer, LinkedList<OrderTask>> tasks;
    private String taskGourpName = "MapTaskGroup#" + this.hashCode();
    private boolean isDone = false;
    private boolean isSuccess = false;
    private R result ;

    public MapTaskGroup(String taskGourpName) {
        this();
        this.taskGourpName = taskGourpName;
    }

    public MapTaskGroup() {
        tasks = new TreeMap<Integer, LinkedList<OrderTask>>(Integer::compare);
    }

    @Override
    public R call(){
        work();
        return result;
    }

    @Override
    public void work() {
        Set<Integer> keySet = tasks.keySet();
        Iterator<Integer> iterator = keySet.iterator();
        try {
            while (iterator.hasNext()) {
                LinkedList<OrderTask> orderTasks = tasks.get(iterator.next());
                if (orderTasks.size() > 1) {
                    List<R> list = new LinkedList();
                    for (OrderTask orderTask : orderTasks) {
                        R executeResult = (R)orderTask.execute(result);
                        list.add(executeResult);
                    }
                    result =(R)list;
                } else {
                    result =(R)orderTasks.get(0).execute(result);
                }
            }
            workDone();
        } catch (QueryException e) {
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
        if (tasks.containsKey(task.getOrder())) {
            return tasks.get(task.getOrder()).add(task);
        } else {
            LinkedList list = new LinkedList<OrderTask>();
            list.add(task);
            tasks.put(task.getOrder(), list);
            return true;
        }
    }

    @Override
    public boolean add(Collection<OrderTask> collection) {
        Iterator<OrderTask> iterator = collection.iterator();
        while (iterator.hasNext()) {
            OrderTask task = iterator.next();
            if (tasks.containsKey(task.getOrder())) {
                tasks.get(task.getOrder()).add(task);
            } else {
                tasks.put(task.getOrder(), new LinkedList<OrderTask>()).add(task);
            }
        }
        return true;
    }

    @Override
    public boolean remove(OrderTask task) {
        if (tasks.containsKey(task.getOrder())) {
            return tasks.get(task.getOrder()).remove(task);
        } else return true;
    }

    @Override
    public boolean removeAll() {
        tasks.clear();
        return true;
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
}
