package com.hzoom.demo.activiti;

import com.hzoom.demo.EurekaClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EurekaClientApplication.class)
@Slf4j
public class MDCErrorDelegateTest {
    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

    @Test
    public void test() {
        LogMDC.setMDCEnabled(true);
        RepositoryService repositoryService = processEngineConfiguration.buildProcessEngine().getRepositoryService();
        RuntimeService runtimeService = processEngineConfiguration.buildProcessEngine().getRuntimeService();
        TaskService taskService = processEngineConfiguration.buildProcessEngine().getTaskService();
        FormService formService = processEngineConfiguration.buildProcessEngine().getFormService();
        HistoryService historyService = processEngineConfiguration.buildProcessEngine().getHistoryService();

        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/MDCErrorDelegate.bpmn").deploy();
        log.info("deploy : {}", deploy.getName());

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("PROCESS_2");
        log.info("instance : {}", instance.getName());

        Task task = taskService.createTaskQuery().singleResult();
        log.info("task {} ",task.getName());
    }
}
