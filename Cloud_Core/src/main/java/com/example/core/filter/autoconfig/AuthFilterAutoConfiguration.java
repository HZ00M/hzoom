package com.example.core.filter.autoconfig;

import com.example.core.filter.component.LogFilter;
import com.example.core.filter.registrationbean.AuthFilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({AuthFilterRegistrationBean.class, LogFilter.class})
public class AuthFilterAutoConfiguration {
    @Bean("authFilterRegistrationBean")
    @ConditionalOnMissingBean({AuthFilterRegistrationBean.class})
    public AuthFilterRegistrationBean filterRegistrationBean(){
        return new AuthFilterRegistrationBean();
    }
}
