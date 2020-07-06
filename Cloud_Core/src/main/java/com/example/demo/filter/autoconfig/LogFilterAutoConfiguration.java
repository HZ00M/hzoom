package com.example.demo.filter.autoconfig;

import com.example.demo.filter.registrationbean.LogFilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnBean({LogFilterRegistrationBean.class})
public class LogFilterAutoConfiguration {
    @Bean("logFilterRegistrationBean")
    @ConditionalOnMissingBean({LogFilterRegistrationBean.class})
    public LogFilterRegistrationBean filterRegistrationBean(){
        return new LogFilterRegistrationBean();
    }
}
