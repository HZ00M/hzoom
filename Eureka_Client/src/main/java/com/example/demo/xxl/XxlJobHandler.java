package com.example.demo.xxl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;

public class XxlJobHandler {
//    @XxlJob(value = "demoJobHandler")
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("XXL-JOB, testJobHandler.");
        System.out.println("XXL-JOB测试");
        return ReturnT.SUCCESS;
    }
}
