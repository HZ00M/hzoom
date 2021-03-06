package com.hzoom.demo.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSouceConfig  {

    @Autowired
    DruidProperties druidProperties;

    /**
     * 主数据源
     *
     * @return
     */
    @Primary
    @Bean(name = "defaultDatasource")
    public DataSource defaultDatasource() {

        /**
         * 默认数据源
         */
        //配置多数据源
        Map<Object, Object> datasouces = new HashMap<>();
        datasouces.put(DataSourceEnum.CLOUD0.withType(DataSourceEnum.Type.WRITE), masterCloudDataSource(druidProperties));
        datasouces.put(DataSourceEnum.CLOUD1.withType(DataSourceEnum.Type.WRITE), masterCloud1DataSource(druidProperties));
        datasouces.put(DataSourceEnum.CLOUD0.withType(DataSourceEnum.Type.READ), slaveCloud2DataSource(druidProperties));
        DynamicDataSource dynamicDataSource = new DynamicDataSource(masterCloudDataSource(druidProperties), datasouces);
        return dynamicDataSource;
    }

    @Bean(name = "cloud0")
    @ConfigurationProperties("spring.datasource.druid.master.cloud0")
    public DataSource masterCloudDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "cloud1")
    @ConfigurationProperties("spring.datasource.druid.master.cloud1")
    public DataSource masterCloud1DataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }


    @Bean(name = "cloud2")
    @ConfigurationProperties("spring.datasource.druid.slave.cloud2")
    public DataSource slaveCloud2DataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DynamicDataSourceTransactionManager(defaultDatasource());
    }
}
