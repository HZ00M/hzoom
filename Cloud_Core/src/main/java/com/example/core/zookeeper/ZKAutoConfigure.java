package com.example.core.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


import java.util.Map;

@Configuration
@EnableConfigurationProperties({ZKProperties.class})
@ConditionalOnClass
@Import({ZKUtils.class})
@ConditionalOnProperty(
        prefix = "zk",
        name = {"autoConfigurable"},
        havingValue = "true",
        matchIfMissing = false
)
public class ZKAutoConfigure implements ApplicationContextAware {
    @Autowired
    ZKProperties zkProperties;

    @Autowired
    ApplicationContext context;

    @ConditionalOnMissingBean(CuratorFramework.class)
    @Bean
    public  CuratorFramework curatorFramework() {
        // 重试策略:第一次重试等待1s，第二次重试等待2s，第三次重试等待4s
        // 第一个参数：等待时间的基础单位，单位为毫秒
        // 第二个参数：最大重试次数
        Map<String, RetryPolicy> retryPolicyMap = context.getBeansOfType(RetryPolicy.class);
        RetryPolicy retryPolicy ;
        if (retryPolicyMap.size()>0){
            retryPolicy = retryPolicyMap.get(0);
        }else {
            retryPolicy = new ExponentialBackoffRetry(1000, 3);
        }
        // 获取 CuratorFramework 实例的最简单的方式
        // 第一个参数：zk的连接地址
        // 第二个参数：重试策略
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkProperties.getConnectionString(), retryPolicy);
        client.start();
        return client;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
