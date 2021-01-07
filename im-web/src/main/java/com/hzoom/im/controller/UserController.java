package com.hzoom.im.controller;

import com.hzoom.im.balance.ImLoadBalance;
import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.entity.LoginBack;
import com.hzoom.im.po.UserPO;
import com.hzoom.im.stream.define.StreamClient;
import com.hzoom.im.stream.service.TopicService;
import com.hzoom.im.utils.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/user")
@RefreshScope
public class UserController {
    @Resource
    private ImLoadBalance imLoadBalance;
    @Autowired
    StreamClient streamClient;
    @Autowired
    private TopicService topicService;

    @GetMapping(value = "/login")
    public Mono<String> loginAction(@RequestParam("username") String username,
                                    @RequestParam("password") String password){
        UserPO user = new UserPO();
        user.setUserName(username);
        user.setPassWord(password);
        user.setUserId(user.getUserName());

//        User loginUser = userService.login(user);

        LoginBack back = new LoginBack();
        /**
         * 取得最佳的Netty服务器
         */
        ImNode bestWorker = imLoadBalance.getBestWorker();
        back.setImNode(bestWorker);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        back.setUserDTO(userDTO);
        back.setToken(user.getUserId().toString());

        String r = JsonUtil.pojoToJson(back);
        return Mono.just(r);

    }

    @GetMapping(value = "/removeWorkers")
    public Mono<String> removeWorkers(){
        imLoadBalance.removeWorkers();
        return Mono.just("已经删除");
    }

    @Value("${username}")
    private String username;
    @GetMapping(value = "/username")
    public Mono<String> username(){
        return Mono.just(username);
    }

    @GetMapping(value = "/sendTest")
    public void send(){
        streamClient.output().send(MessageBuilder.withPayload("hello world ...").build());
    }

    @GetMapping(value = "/topics/{target}")
    public void send(@PathVariable("target") String target){
        topicService.sendMessage("my topic test",target);
    }
}
