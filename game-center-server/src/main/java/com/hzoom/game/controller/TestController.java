package com.hzoom.game.controller;


import com.hzoom.core.datasource.anatation.DataSource;
import com.hzoom.core.datasource.enums.DataSourceType;
import com.hzoom.game.common.entity.TUser;
import com.hzoom.game.common.mapper.TUserMapper;
import com.hzoom.game.common.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testng.annotations.Test;

import java.util.List;


@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TUserMapper tUserMapper;

    @Autowired
    private TUserService userService;

    @Value("${server.port}")
    private int port;

    @RequestMapping("/https")
    public Object getHtts() {
        return "Hello ,Https" +port;
    }


    @RequestMapping("/add")
    public void addUser(){
        TUser user = new TUser();
        user.setNickname("nickName");
        user.setPassword("123");
        user.setUsername("userName");
        tUserMapper.inserts(user);
    }

    @RequestMapping("/select")
    public List<TUser> select(){
        List<TUser> select = tUserMapper.select();
        return select;
    }

    @RequestMapping("/list")
    public List<TUser> list(){
        return userService.list();
    }

    @RequestMapping("/list2")
    @DataSource(value = "cloud2",type = DataSourceType.WRITE)
    public List<TUser> list2(){
        return userService.list();
    }

    @RequestMapping("/list3")
    @DataSource(value = "cloud2")
    public List<TUser> list3(){
        return userService.list();
    }
}
