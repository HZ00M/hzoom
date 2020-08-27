package com.example.demo.controller;

import com.example.demo.datasource.DataSource;
import com.example.demo.datasource.DataSourceEnum;
import com.example.demo.domain.User;
import com.example.demo.interceptor.PageParam;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add1")
    @DataSource(DataSourceEnum.CLOUD1)
    public int add1(@RequestBody User user) {
       return userService.add(user);
    }

    @PostMapping("/add2")
    @DataSource(DataSourceEnum.CLOUD)
    public int add2(@RequestBody User user) {
        return userService.add(user);
    }

    @PostMapping("/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    @PostMapping("/selectCloudAuto")
    public List<User> selectCloudAuto() {
        return userService.select();
    }

    @PostMapping("/selectCloud")
    @DataSource(value = DataSourceEnum.CLOUD,type = DataSourceEnum.Type.WRITE)
    public List<User> selectCloud() {
        return userService.select();
    }
    @PostMapping("/selectCloud1")
    @DataSource(value = DataSourceEnum.CLOUD1,type = DataSourceEnum.Type.WRITE)
    public List<User> selectCloud1() {
        return userService.select();
    }

    @PostMapping("/selectCloud2")
    @DataSource(value = DataSourceEnum.CLOUD,type = DataSourceEnum.Type.READ)
    public List<User> selectCloud2() {
        return userService.select();
    }

    @PostMapping("/selectTransactional")
    @Transactional
    @DataSource(value = DataSourceEnum.CLOUD,type = DataSourceEnum.Type.READ)
    public List<User> selectTransactional() {
        return userService.select();
    }

}
