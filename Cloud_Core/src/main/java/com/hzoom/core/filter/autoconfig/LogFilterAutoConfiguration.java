package com.hzoom.core.filter.autoconfig;

import com.hzoom.core.filter.component.AuthFilter;
import com.hzoom.core.filter.registrationbean.LogFilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({LogFilterRegistrationBean.class, AuthFilter.class})
public class LogFilterAutoConfiguration {
    @Bean("logFilterRegistrationBean")
    @ConditionalOnMissingBean({LogFilterRegistrationBean.class})
    public LogFilterRegistrationBean filterRegistrationBean(){
        return new LogFilterRegistrationBean();
    }
}
