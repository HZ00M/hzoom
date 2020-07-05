package com.example.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean({FilterRegistrationBean.class})
public class FilterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({FilterRegistrationBean.class})
    public FilterRegistrationBean logFilterRegistrationBean(){
        return new FilterRegistrationBean();
    }
}
