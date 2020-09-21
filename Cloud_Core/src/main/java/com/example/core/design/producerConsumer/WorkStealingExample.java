package com.example.core.design.producerConsumer;


import com.example.core.design.twoPhaseTermination.TerminationToken;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class WorkStealingExample {
    private final WorkStealingEnabledChannel<String> channel;
    private final TerminationToken token = new TerminationToken();

    public WorkStealingExample() {
        int nCPU = Runtime.getRuntime().availableProcessors();
        int consumerCount = nCPU / 2 + 1;

        BlockingQueue<String>[] managedQueues = new LinkedBlockingDeque[consumerCount];

        //该通道实例对于了多个队列实例 manageQueue
        channel = new WorkStealingChannel<String>(managedQueues);
        Consumer[] consumers = new Consumer[consumerCount];
        for (int i = 0; i < nCPU; i++) {
            managedQueues[i] = new LinkedBlockingDeque<String>();
        }
    }
}
