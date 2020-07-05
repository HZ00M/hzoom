package com.example.demo.config;

import com.example.demo.filter.LogFilter;

public class FilterRegistrationBean extends org.springframework.boot.web.servlet.FilterRegistrationBean<LogFilter> {
    public FilterRegistrationBean() {
        super();
        this.setFilter(new LogFilter());
        this.addUrlPatterns("*/*");
        this.setName("myLogFilter");
        this.setOrder(1);
    }
}
