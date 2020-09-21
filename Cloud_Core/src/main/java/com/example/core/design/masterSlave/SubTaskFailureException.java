package com.example.core.design.masterSlave;

/**
 * 子任务异常
 */
public class SubTaskFailureException extends Exception {
    /**
     * 对处理失败的子任务进行失败重试所需的信息
     */
    public final RetryInfo retryInfo;

    public SubTaskFailureException(RetryInfo retryInfo, Exception caues){
        super(caues);
        this.retryInfo = retryInfo;
    }
}
