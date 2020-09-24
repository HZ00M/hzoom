package com.example.demo.schedule.taskGroup;


import com.example.demo.schedule.task.OrderTask;

import java.util.Collection;

/**
 *   操作定义
 */
public interface Operable {
    boolean add(OrderTask task);

    boolean add(Collection<OrderTask> collection);

    boolean remove(OrderTask task);

    boolean removeAll();
}
