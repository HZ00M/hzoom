package com.hzoom.demo;

import com.hzoom.core.filter.annotation.EnableFilter;
import com.hzoom.core.filter.enums.FilterAutoConfigurationEnum;
import com.hzoom.core.xxl.annotation.EnableXxlJob;
import org.activiti.spring.boot.DataSourceProcessEngineAutoConfiguration;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableFilter({FilterAutoConfigurationEnum.Auth})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class, DataSourceProcessEngineAutoConfiguration.class, SpringBootConfiguration.class})
@EnableXxlJob
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }
}
