package com.example.core.xxl.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlProperties {
    public XxlProperties(){}
    private Admin admin = new Admin();
    private Executor executor = new Executor();

    @Data
    class Admin{
        private String addresses = "http://127.0.0.1:9056/xxl-job-admin";;
    }
    @Data
    public class Executor{
        private String appName;
        private int logRetentionDays;
        private String logPath;
        private String ip;
        private int port;
    }
}
