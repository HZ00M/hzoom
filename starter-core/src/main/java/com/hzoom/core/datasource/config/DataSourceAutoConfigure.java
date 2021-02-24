package com.hzoom.core.datasource.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.hzoom.core.datasource.DynamicDataSource;
import com.hzoom.core.datasource.DynamicDataSourceContextHolder;
import com.hzoom.core.datasource.DynamicDataSourceTransactionManager;
import com.hzoom.core.datasource.aspect.DataSourceAspect;
import com.hzoom.core.datasource.interceptor.DynamicInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({DruidDataSourceProperties.class})
@ConditionalOnClass
@ConditionalOnProperty(
        prefix = "spring.datasource",
        name = {"enable"},
        havingValue = "true",
        matchIfMissing = false
)
public class DataSourceAutoConfigure {
    @Autowired
    private DruidDataSourceProperties dataSourceProperties;

    /**
     * 主数据源
     *
     * @return
     */
    @Primary
    @Bean
    public DataSource defaultDatasource() {

        /**
         * 默认数据源
         */
        //配置多数据源
        Map<Object, Object> datasouces = getDataSources();
        DynamicDataSourceContextHolder.setDefaultDbName(dataSourceProperties.getMap().get(dataSourceProperties.getMaster()).getDatabaseName());
        DynamicDataSource dynamicDataSource = new DynamicDataSource((DataSource)datasouces.get(dataSourceProperties.getMaster()), datasouces);
        return dynamicDataSource;
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DynamicDataSourceTransactionManager(defaultDatasource());
    }

    public Map<Object, Object>  getDataSources() throws BeansException {
        Set<Map.Entry<String, SourceProperties>> entries = dataSourceProperties.getMap().entrySet();
        Map<Object, Object> dataSources = new HashMap();
        DruidProperties druidProperties = dataSourceProperties.getDruid();
        for (Map.Entry<String, SourceProperties> datasourceEntry : entries) {
            String databaseName = datasourceEntry.getValue().getDatabaseName();
            SourceProperties DataSourceProperties = datasourceEntry.getValue();
            DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
            DruidDataSource wrapper = wrapper(dataSource,druidProperties,DataSourceProperties);
            dataSources.put(databaseName+"-"+DataSourceProperties.getDataSourceType(),wrapper);
        }
        return dataSources;
    }

    public DruidDataSource wrapper(DruidDataSource datasource, DruidProperties druidProperties, SourceProperties sourceProperties)
    {
        /** 配置初始化大小、最小、最大 */
        datasource.setDriverClassName(dataSourceProperties.getDriverClassName());
        datasource.setInitialSize(druidProperties.getInitialSize());
        datasource.setMaxActive(druidProperties.getMaxActive());
        datasource.setMinIdle(druidProperties.getMinIdle());

        /** 配置获取连接等待超时的时间 */
        datasource.setMaxWait(druidProperties.getMaxWait());

        /** 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(druidProperties.getTimeBetweenEvictionRunsMillis());

        /** 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
        datasource.setMinEvictableIdleTimeMillis(druidProperties.getMinEvictableIdleTimeMillis());
        datasource.setMaxEvictableIdleTimeMillis(druidProperties.getMaxEvictableIdleTimeMillis());

        /**
         * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
         */
        datasource.setValidationQuery(druidProperties.getValidationQuery());
        /** 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
        datasource.setTestWhileIdle(druidProperties.isTestWhileIdle());
        /** 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnBorrow(druidProperties.isTestOnBorrow());
        /** 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnReturn(druidProperties.isTestOnReturn());

        datasource.setUrl(sourceProperties.getUrl());
        datasource.setUsername(sourceProperties.getUsername());
        datasource.setPassword(sourceProperties.getPassword());

        return datasource;
    }

    @Bean
    public DynamicInterceptor dynamicInterceptor(){
        return new DynamicInterceptor();
    }
    @Bean
    public DataSourceAspect dataSourceAspect(){
        return new DataSourceAspect();
    }
}
