package com.example.demo.xxl;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


//@Configuration
@Slf4j
public class XxlConfig {
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;
    @Value("${xxl.job.executor.appname}")
    private String appName;
    @Value("${xxl.job.executor.logretentiondays}")
    private Integer logretentiondays;
    @Value("${xxl.job.executor.logpath}")
    private String logpath;
//    @Value("${xxl.job.executor.ip}")
//    private String ip;
//    @Value("${xxl.job.executor.port}")
//    private Integer port;

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(){
        log.info("====xxl-job config init====");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setLogRetentionDays(logretentiondays);
        xxlJobSpringExecutor.setLogPath(logpath);
//        xxlJobSpringExecutor.setIp(ip);
//        xxlJobSpringExecutor.setPort(port);
        return xxlJobSpringExecutor;
    }
}
