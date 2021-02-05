package com.hzoom.game.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.hzoom.core.datasource.config.DruidProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@ConfigurationProperties("spring.datasource.druid")
@Configuration
public class DataSourceConfig {

    @Bean(name = "cloud0")
    @ConfigurationProperties("spring.datasource.map.cloud0")
    public DataSource masterCloudDataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean(name = "cloud1")
    @ConfigurationProperties("spring.datasource.map.cloud1")
    public DataSource masterCloud1DataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }


    @Bean(name = "cloud2")
    @ConfigurationProperties("spring.datasource.map.cloud2")
    public DataSource slaveCloud2DataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }
}
