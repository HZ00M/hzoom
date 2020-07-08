package com.example.core.xxl.autoconfig;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({XxlProperties.class})
@ConditionalOnProperty(
        prefix = "xxl.job",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties({XxlProperties.class})
public class XxlAutoConfiguration {
    private final XxlProperties xxlProperties;
    public XxlAutoConfiguration(XxlProperties xxlProperties){
        this.xxlProperties = xxlProperties;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(){
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlProperties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(xxlProperties.getExecutor().getAppName());
        xxlJobSpringExecutor.setLogRetentionDays(xxlProperties.getExecutor().getLogRetentionDays());
        xxlJobSpringExecutor.setLogPath(xxlProperties.getExecutor().getLogPath());
        xxlJobSpringExecutor.setIp(xxlProperties.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlProperties.getExecutor().getPort());
        return xxlJobSpringExecutor;
    }
}
