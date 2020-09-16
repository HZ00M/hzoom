package com.example.demo.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.yaml.swapper.impl.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.ShardingRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;


import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({
        SpringBootShardingRuleConfigurationProperties.class,
        SpringBootMasterSlaveRuleConfigurationProperties.class, SpringBootEncryptRuleConfigurationProperties.class, SpringBootPropertiesConfigurationProperties.class})
@ConfigurationProperties("spring.datasource.druid")
public class DataSouceConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    DruidProperties druidProperties;
    @Autowired
    private SpringBootShardingRuleConfigurationProperties shardingRule;

    @Autowired
    private SpringBootPropertiesConfigurationProperties props;

    /**
     * 主数据源
     *
     * @return
     */
    @Primary
    @Bean(name = "default")
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

    @Bean("shardingDataSource")
    @Conditional(ShardingRuleCondition.class)
    public DataSource shardingDataSource() throws SQLException {
        // 获取其它方式配置的数据源
        Map<String, DruidDataSource> beans = applicationContext.getBeansOfType(DruidDataSource.class);
        Map<String, DataSource> dataSourceMap = new HashMap<>(4);
        beans.forEach(dataSourceMap::put);
        // 创建shardingDataSource
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, new ShardingRuleConfigurationYamlSwapper().swap(shardingRule), props.getProps());
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 将shardingDataSource设置到SqlSessionFactory中
        sqlSessionFactoryBean.setDataSource(shardingDataSource());
        // 其它设置
        return sqlSessionFactoryBean.getObject();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
