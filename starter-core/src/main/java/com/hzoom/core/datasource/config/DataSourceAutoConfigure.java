package com.hzoom.core.datasource.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.hzoom.core.datasource.DynamicDataSource;
import com.hzoom.core.datasource.DynamicDataSourceTransactionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
        prefix = "datasource",
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
    @Bean(name = "defaultDatasource")
    public DataSource defaultDatasource() {

        /**
         * 默认数据源
         */
        //配置多数据源
        Map<Object, Object> datasouces = getDataSources();
        DynamicDataSource dynamicDataSource = new DynamicDataSource((DataSource)datasouces.get("master"), datasouces);
        return dynamicDataSource;
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DynamicDataSourceTransactionManager(defaultDatasource());
    }

    public Map<Object, Object>  getDataSources() throws BeansException {
        Set<Map.Entry<String, DruidProperties>> entries = dataSourceProperties.getMap().entrySet();
        Map<Object, Object> dataSources = new HashMap();
        for (Map.Entry<String, DruidProperties> datasourceEntry : entries) {
            String name = datasourceEntry.getKey();
            DruidProperties druidProperties = datasourceEntry.getValue();
            DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
            DruidDataSource wrapper = druidProperties.wrapper(dataSource);
            dataSources.put(name+"-"+druidProperties.getType(),wrapper);
        }
        return dataSources;
    }

}
