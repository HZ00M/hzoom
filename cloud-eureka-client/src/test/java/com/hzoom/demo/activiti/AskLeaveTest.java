package com.hzoom.demo.activiti;

import com.hzoom.demo.EurekaClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EurekaClientApplication.class)
@Slf4j
public class AskLeaveTest {
    @Autowired
    ProcessEngine processEngine;

    @Test
    public void test() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        FormService formService = processEngine.getFormService();
        HistoryService historyService = processEngine.getHistoryService();

        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/ask_leave.bpmn").deploy();
        log.info("deploy : {}", deploy.getId());

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        log.info("processDefinition : {}", processDefinition.getId());

        ProcessInstance instance = runtimeService.startProcessInstanceById(processDefinition.getId());
        log.info("instance : {}", instance.getId());
        List<Task> tasks = taskService.createTaskQuery().active().listPage(0, 100);
        while (!tasks.isEmpty()) {
            for (Task task : tasks) {
                if (task.getName().equals("主管审批")){
                    TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                    Map<String, String> fromValues = new HashMap<>();
                    for (FormProperty property : taskFormData.getFormProperties()) {
                        log.info("请输入 [{}]", property.getName());
                        fromValues.put(property.getId(), "y");
                    }
                    formService.submitTaskFormData(task.getId(), fromValues);
                }
                if (task.getName().equals("填写请假申请")){
                    TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                    Map<String, String> fromValues = new HashMap<>();
                    for (FormProperty property : taskFormData.getFormProperties()) {
                        log.info("请输入 [{}]", property.getName());
                        fromValues.put(property.getId(), property.getName());
                    }
                    formService.submitTaskFormData(task.getId(), fromValues);
                }
                if (task.getName().equals("HR审批")){
                    TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                    Map<String, String> fromValues = new HashMap<>();
                    for (FormProperty property : taskFormData.getFormProperties()) {
                        log.info("请输入 [{}]", property.getName());
                        fromValues.put(property.getId(), "y");
                    }
                    formService.submitTaskFormData(task.getId(), fromValues);
                }
             }
            tasks = taskService.createTaskQuery().active().listPage(0, 100);
        }
        log.info("instance {} 结束",instance.getId());
    }

}
