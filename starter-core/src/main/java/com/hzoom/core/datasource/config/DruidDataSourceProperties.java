package com.hzoom.core.datasource.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DruidDataSourceProperties {
    private String master;

    private Map<String, SourceProperties> map;

    private DruidProperties druid;

    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
