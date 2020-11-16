package com.hzoom.demo.jsonTest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzoom.demo.po.User;
import org.junit.Test;

public class JsonTest {
    @Test
    public void test(){
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        String s = JSON.toJSONString(user);
        User user1 = JSONObject.parseObject(s, User.class);
    }
}
