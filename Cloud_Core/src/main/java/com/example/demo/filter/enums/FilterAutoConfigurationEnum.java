package com.example.demo.filter.enums;

import com.example.demo.filter.autoconfig.AuthFilterAutoConfiguration;
import com.example.demo.filter.autoconfig.LogFilterAutoConfiguration;

public enum FilterAutoConfigurationEnum {

    LOG(LogFilterAutoConfiguration.class.getName()),Auth(AuthFilterAutoConfiguration.class.getName());

    private String filterName;

    FilterAutoConfigurationEnum(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
