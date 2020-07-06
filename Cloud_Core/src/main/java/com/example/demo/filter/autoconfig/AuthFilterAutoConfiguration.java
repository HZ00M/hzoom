package com.example.demo.filter.autoconfig;

import com.example.demo.filter.registrationbean.AuthFilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnBean({AuthFilterRegistrationBean.class})
public class AuthFilterAutoConfiguration {
    @Bean("authFilterRegistrationBean")
    @ConditionalOnMissingBean({AuthFilterRegistrationBean.class})
    public AuthFilterRegistrationBean filterRegistrationBean(){
        return new AuthFilterRegistrationBean();
    }
}
