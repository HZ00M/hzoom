package com.hzoom.core.xxl.autoconfig;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@ConditionalOnClass({XxlProperties.class})
@EnableConfigurationProperties({XxlProperties.class})
public class XxlAutoConfiguration {

    private final XxlProperties xxlProperties;
    public XxlAutoConfiguration(XxlProperties xxlProperties){
        this.xxlProperties = xxlProperties;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "xxl.job",
            name = {"enabled"},
            havingValue = "true",
            matchIfMissing = true
    )
    public XxlJobSpringExecutor xxlJobSpringExecutor(){
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlProperties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(xxlProperties.getExecutor().getAppName());
        xxlJobSpringExecutor.setLogRetentionDays(xxlProperties.getExecutor().getLogRetentionDays());
        xxlJobSpringExecutor.setLogPath(xxlProperties.getExecutor().getLogPath());
        Optional.ofNullable(xxlProperties.getExecutor().getIp()).ifPresent(xxlJobSpringExecutor::setIp);
        Optional.ofNullable(xxlProperties.getExecutor().getPort()).ifPresent(xxlJobSpringExecutor::setPort);
        return xxlJobSpringExecutor;
    }
}
