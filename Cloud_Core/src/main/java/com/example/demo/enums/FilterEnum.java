package com.example.demo.enums;

import com.example.demo.filter.AuthFilter;
import com.example.demo.filter.LogFilter;

import javax.servlet.Filter;

public enum  FilterEnum{

    LOG("log",LogFilter.class),Auth("auth",AuthFilter.class);

    private String filterName;
    private Class<? extends Filter> filter;


    FilterEnum(String filterName, Class<? extends Filter> filter) {
        this.filterName = filterName;
        this.filter = filter;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Class<? extends Filter> getFilter() {
        return filter;
    }

    public void setFilter(Class<? extends Filter> filter) {
        this.filter = filter;
    }
}
