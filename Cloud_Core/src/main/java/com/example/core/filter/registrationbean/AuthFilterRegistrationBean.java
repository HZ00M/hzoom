package com.example.core.filter.registrationbean;

import com.example.core.filter.component.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

public class AuthFilterRegistrationBean extends FilterRegistrationBean<AuthFilter> {
    public AuthFilterRegistrationBean() {
        super();
        this.setFilter(new AuthFilter());
        this.addUrlPatterns("*");
        this.setName("myAuthFilter");
        this.setOrder(1);
    }
}
