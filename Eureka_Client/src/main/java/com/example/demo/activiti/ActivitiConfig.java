package com.example.demo.activiti;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiConfig {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;


    // activiti core
    // ProcessEngine 和服务类都是线程安全的。

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration() {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        // 连接数据库的配置
        processEngineConfiguration.setJdbcDriver(driver);
        processEngineConfiguration.setJdbcUrl(url);
        processEngineConfiguration.setJdbcUsername(userName);
        processEngineConfiguration.setJdbcPassword(password);
        processEngineConfiguration.setAsyncExecutorActivate(true);
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        return processEngineConfiguration;
    }

    /* @Bean
     public ProcessEngine processEngine() {
         return processEngineConfiguration().buildProcessEngine();
     }
     */
    @Bean
    public RepositoryService repositoryService() {
        return processEngineConfiguration().buildProcessEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngineConfiguration().buildProcessEngine().getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        return processEngineConfiguration().buildProcessEngine().getTaskService();
    }

    @Bean
    public FormService formService() {
        return processEngineConfiguration().buildProcessEngine().getFormService();
    }

    @Bean
    public HistoryService historyService() {
        return processEngineConfiguration().buildProcessEngine().getHistoryService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngineConfiguration().buildProcessEngine().getManagementService();
    }

    @Bean
    public IdentityService identityService() {
        return processEngineConfiguration().buildProcessEngine().getIdentityService();
    }
}
