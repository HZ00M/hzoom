package com.example.core.design.twoPhaseTermination;


import com.example.core.design.guardedSupension.AlarmAgent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模式角色 ConcreteTerminatableThread
 */
public class AlarmSendingThread extends AbstractTerminatableThread{

    private final AlarmAgent alarmAgent = new AlarmAgent();

    private final BlockingQueue<String> alarmQueue;

    private final ConcurrentMap<String, AtomicInteger> submittedAlarmRegistry;

    public AlarmSendingThread(){
        alarmQueue = new ArrayBlockingQueue<String>(100);
        submittedAlarmRegistry = new ConcurrentHashMap<>();
        alarmAgent.init();
    }

    @Override
    protected void doRun() throws Exception {
        String alarmInfo;
        alarmInfo = alarmQueue.take();
        terminationToken.reservation.decrementAndGet();
        try {
            alarmAgent.sendAlarm(alarmInfo);
        }catch (Exception e){
            e.printStackTrace();
        }

        /**
         * 处理恢复告警：将相应的故障告警从注册表中删除，使得相应故障恢复后再次使用相同的故障，该故障信息能够上报到告警服务器
         */

    }

    public int sendAlarm(final String alarmInfo){
        if (terminationToken.isToShutdown()){
            return -1;
        }
        int duplicateSubmissonCounter = 0 ;
        try {
            AtomicInteger preSubmittedCounter;
            preSubmittedCounter = submittedAlarmRegistry.putIfAbsent(alarmInfo,new AtomicInteger());
            if (null == preSubmittedCounter){
                terminationToken.reservation.incrementAndGet();
                alarmQueue.put(alarmInfo);
            }else {
                //故障未恢复。不用重复发送告警信息给服务器，故仅增加计数
                duplicateSubmissonCounter = preSubmittedCounter.incrementAndGet();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return duplicateSubmissonCounter;
    }
}
