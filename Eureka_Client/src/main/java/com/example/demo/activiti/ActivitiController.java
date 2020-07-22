package com.example.demo.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class ActivitiController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

    /**
     * 一个示例流程
     *
     * @return
     */
    @GetMapping("/process")
    public void process() {

        // 先准备好用户、用户组
        addUser();
        addGroup();
        relationUserGroup();

        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/ask_leave.bpmn")
//                .name("test inspect")
//                .key("shOffice")
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        log.info("流程名称 ： [" + processDefinition.getName() + "]， 流程ID ： ["
                + processDefinition.getId() + "], 流程KEY : " + processDefinition.getKey());

        // 开始流程
        String procId = runtimeService.startProcessInstanceByKey(processDefinition.getKey()).getId();

        /*// 给一个新任务添加候选人组student
        Task sTask = taskService.newTask();
        taskService.addCandidateGroup(sTask.getId(), "student");*/

        // 获取指定用户组的 Task 列表,并使用指定用户领取这些任务
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("student").list();
        for (Task task : tasks) {
            System.out.println("student分组的任务: " + task.getName());
            // 用户Jonathan领取任务
            taskService.claim(task.getId(), "Jonathan");
        }

        // 获取Jonathan用户的工作 Task 列表，并完成这些任务
        tasks = taskService.createTaskQuery().taskAssignee("Jonathan").list();
        for (Task task : tasks) {
            log.info("Jonathan的任务: " + task.getName());
            // 完成
            taskService.complete(task.getId());
        }

        log.info("用户Jonathan的任务数量: " +
                taskService.createTaskQuery().taskAssignee("Jonathan").count());

        // 使用 HistoryService 来查询指定流程实例的状态
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
        log.info("流程结束时间: " + historicProcessInstance.getEndTime());
    }


    public void addUser() {
        //先查询用户
        User userInDb = identityService.createUserQuery().userId("Jonathan").singleResult();
        if (userInDb == null) {
            User user = identityService.newUser("Jonathan");
            user.setFirstName("Jonathan");
            user.setLastName("chang");
            user.setEmail("whatlookingfor@gmail.com");
            user.setPassword("123");
            //保存用户到数据库
            identityService.saveUser(user);
        }
    }

    public void addGroup() {
        Group groupInDb = identityService.createGroupQuery().groupId("student").singleResult();
        if (groupInDb == null) {
            //创建用户组对象
            Group group = identityService.newGroup("student");
            group.setName("student用户组");
            group.setType("ask");
            //保存用户组
            identityService.saveGroup(group);
        }
    }

    public void relationUserGroup() {
        // 查询属于student用户组的用户
        User userInGroup = identityService.createUserQuery().memberOfGroup("student").singleResult();
        User userInDb = identityService.createUserQuery().userId("Jonathan").singleResult();
        if (userInGroup == null || !userInGroup.getId().equals(userInDb.getId())) {
            //将用户Jonathan加入到用户组student中
            identityService.createMembership("Jonathan", "student");
        }
    }
}
