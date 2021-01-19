package com.hzoom.game.pojo;

import lombok.Data;

@Data
public class TaskDataConfig {
    public String taskId;//任务id
    public String preTaskId;//上一个任务的id
    public String nextTaskId;//下一个任务的id
    public int taskType; //任务类型
    public String param; //任务参数
    public String rewardId; //任务奖励id
}
