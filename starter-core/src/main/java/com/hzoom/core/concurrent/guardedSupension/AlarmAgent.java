package com.hzoom.core.concurrent.guardedSupension;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmAgent {

    private volatile boolean connectedToServer = false;

    // Predicate 角色
    private final Predicate agentConnected = () -> connectedToServer;

    //心跳定时器
    private final Timer heartbeatTimer = new Timer(true);

    // Blocker角色
    private final Blocker blocker = new ConditionVarBlocker();


    //执行目标行为
    public void sendAlarm(final String alarmInfo) throws Exception {
        //模式角色 GuardedAction
        GuardedAction<Void> guardedAction = new GuardedAction<Void>(agentConnected) {
            @Override
            public Void call() throws Exception {
                doSendAlarm(alarmInfo);
                return null;
            }
        };
        blocker.callWithGuard(guardedAction);
    }

    //目标行为
    public void doSendAlarm(String alarmInfo) {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
    }

    public void init() {
        Thread connectingThread = new Thread(new ConnectingTask());
        connectingThread.start();
        heartbeatTimer.schedule(new HeartbeatTask(), 60000, 2000);
    }

    protected void onConnected() {
        try {
            blocker.signalAfter(() -> {
                connectedToServer = true;
                return Boolean.TRUE;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDisconnected() {
        connectedToServer = false;
    }

    private class ConnectingTask implements Runnable {

        public void run() {
            //模拟连接操作
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                ;
            }
            onConnected();
        }
    }

    //测试连接状态
    private boolean testConnection() {
        return true;
    }

    //重新连接
    private void reconnect() {
        ConnectingTask connectingTask = new ConnectingTask();
        connectingTask.run();
    }

    //模拟心跳
    private class HeartbeatTask extends TimerTask {

        @Override
        public void run() {
            if (!testConnection()) {
                onDisconnected();
                reconnect();
            }
        }
    }
}
