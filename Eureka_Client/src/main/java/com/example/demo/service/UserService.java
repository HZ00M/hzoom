package com.example.demo.service;

import com.example.core.sqlgen.SearchFiled;
import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public int add(User user) {
        int result = userMapper.add(user);
        return result;
    }

    public void update(User user) {
        userMapper.update(user);
    }

    public void delete(Integer id) {
        User user = new User();
        user.setId(id);
        userMapper.del(user);
    }

    public List<User> select(){
        User user = new User();
        return userMapper.select(user);
    }
}
