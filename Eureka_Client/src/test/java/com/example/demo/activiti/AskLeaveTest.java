package com.example.demo.activiti;

import com.example.demo.EurekaClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EurekaClientApplication.class)
@Slf4j
public class AskLeaveTest {
    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

//    @Rule
//    public ActivitiRule activitiRule =new ActivitiRule("processes/simple.bpmn");
//
//    @Test
//    public void activitiRuleTest(){
//        String name = activitiRule.getProcessEngine().getName();
//        System.out.println(name);
//    }

    @Test
    public void deplaymentTest() {
        RepositoryService repositoryService = processEngineConfiguration.buildProcessEngine().getRepositoryService();
        RuntimeService runtimeService = processEngineConfiguration.buildProcessEngine().getRuntimeService();
        TaskService taskService = processEngineConfiguration.buildProcessEngine().getTaskService();

        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/ask_leave.bpmn").deploy();
        log.info("deploy : {}", deploy.getName());

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        log.info("processDefinition : {}", processDefinition.getName());

        ProcessInstance instance = runtimeService.startProcessInstanceById(processDefinition.getId());
        log.info("instance : {}", instance.getName());
        List<Task> tasks = taskService.createTaskQuery().listPage(0, 100);
        for (Task task : tasks) {
            log.info("task : {}", task.getName());
            taskService.complete(task.getId());
        }
    }

    @Test
    public void test() {
        RepositoryService repositoryService = processEngineConfiguration.buildProcessEngine().getRepositoryService();
        RuntimeService runtimeService = processEngineConfiguration.buildProcessEngine().getRuntimeService();
        TaskService taskService = processEngineConfiguration.buildProcessEngine().getTaskService();
        FormService formService = processEngineConfiguration.buildProcessEngine().getFormService();
        HistoryService historyService = processEngineConfiguration.buildProcessEngine().getHistoryService();

        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/test.bpmn").deploy();
        log.info("deploy : {}", deploy.getName());

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        log.info("processDefinition : {}", processDefinition.getName());

        ProcessInstance instance = runtimeService.startProcessInstanceById(processDefinition.getId());
        log.info("instance : {}", instance.getName());
        Scanner scanner = new Scanner(System.in);
        while (!instance.isEnded()) {
            List<Task> tasks = taskService.createTaskQuery().active().listPage(0, 100);
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
        }
        log.info("instance {} 结束",instance.getName());
    }

}
