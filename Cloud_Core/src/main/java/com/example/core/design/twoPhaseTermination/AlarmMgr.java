package com.example.core.design.twoPhaseTermination;

/**
 * 入口类
 * 模式角色:ThreadOwner
 */
public class AlarmMgr {
    //保存唯一实例
    private final static AlarmMgr alarmMgr= new AlarmMgr();

    private volatile boolean shutdownRequest = false;

    //告警发送线程
    private final AlarmSendingThread alarmSendingThread;

    //私有化构造方法
    private  AlarmMgr(){
     alarmSendingThread = new AlarmSendingThread();
    }

    public static AlarmMgr getInstance(){
        return alarmMgr;
    }

    /**
     * 发送告警
     * type 告警类型
     * id   告警编号
     * extraInfo 告警参数
     * type + id + extraInfo 唯一确定告警信息被提交次数。-1表示告警管理器已经关闭
     */
    public int sendAlarm(String alarmInfo){
        int duplicateSubmissionCount = 0;
        try {
            duplicateSubmissionCount = alarmSendingThread.sendAlarm(alarmInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return duplicateSubmissionCount;
    }


}
