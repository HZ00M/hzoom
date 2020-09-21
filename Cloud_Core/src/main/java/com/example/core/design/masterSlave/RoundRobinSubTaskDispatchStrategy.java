package com.example.core.design.masterSlave;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class RoundRobinSubTaskDispatchStrategy<T, V> implements SubTaskDispatchStrategy<T, V> {


    @Override
    public Iterator<Future<V>> dispatch(Set<? extends SlaveSpec<T, V>> slaves, TaskDivideStrategy<T> taskDivideStrategy) throws InterruptedException {
        final List<Future<V>> subResult = new LinkedList<Future<V>>();
        T subTask;
        Object[] arrSlaves = slaves.toArray();
        int i = -1;
        final int slaveCount = arrSlaves.length;
        Future<V> subTaskFuturePromise;
        while (null != (subTask = taskDivideStrategy.nextTask())) {
            i = (i + 1) % slaveCount;
            subTaskFuturePromise = ((WorkerThreadSlave<T, V>) arrSlaves[i]).submit(subTask);
            subResult.add(subTaskFuturePromise);
        }
        return subResult.iterator();
    }
}
