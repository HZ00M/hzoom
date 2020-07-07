package com.example.core.filter.registrationbean;

import com.example.core.filter.component.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

public class LogFilterRegistrationBean extends FilterRegistrationBean<LogFilter> {
    public LogFilterRegistrationBean() {
        super();
        this.setFilter(new LogFilter());
        this.addUrlPatterns("*");
        this.setName("myLogFilter");
        this.setOrder(1);
    }
}
