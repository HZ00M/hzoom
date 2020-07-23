package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id:\\d+}")
    public User get(@PathVariable Long id) {
        log.info("获取用户id为 " + id + "的信息");
        return new User(id, "mrbird", "123456");
    }

    @GetMapping
    public List<User> get() {
        List<User> list = new ArrayList<>();
        list.add(new User(1L, "mrbird", "123456"));
        list.add(new User(2L, "scott", "123456"));
        log.info("获取用户信息 " + list);
        return list;
    }

    @PostMapping("/add")
    public void add(@RequestBody User user) {
        userService.add(user);
    }

    @PutMapping("/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
