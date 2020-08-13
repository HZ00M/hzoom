package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.interceptor.PageParam;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id:\\d+}")
    public User get(@PathVariable Integer id) {
        log.info("获取用户id为 " + id + "的信息");
        return new User(id, null,null,null);
    }

    @PostMapping
    public List<User> get() {
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(10);
        pageParam.setPage(1);
        return userService.select();
    }

    @PostMapping("/add")
    public int add(User user) {
       return userService.add(user);
    }

    @PostMapping("/update")
    public void update(User user) {
        userService.update(user);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    @PostMapping("/selectWithPage")
    public List<User> selectWithPage(PageParam pageParam) {
        return userService.select();
    }
}
