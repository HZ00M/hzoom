package com.example.demo.jsonTest;

import com.alibaba.fastjson.JSON;
import com.example.demo.po.User;
import org.junit.Test;

public class JsonTest {
    @Test
    public void test(){
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        String s = JSON.toJSONString(user);
    }
}
