package com.hzoom.core.datasource.config;

import com.hzoom.core.datasource.enums.DataSourceType;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;

@Data
public class SourceProperties extends DataSourceProperties {
    private String databaseName;
    private DataSourceType dataSourceType = DataSourceType.AUTO;
}
