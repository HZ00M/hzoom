package com.example.demo.activiti;

import com.example.demo.po.User;
import com.example.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserTest {
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserMapper userMapper;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testTabList() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/update")
                .param("id", "2")
                .param("username", "2")
                .param("password", "2")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();
        result.getResponse().setCharacterEncoding("UTF-8");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void testAdd(){
        User user = new User();
        user.setUsername("name");
        user.setPassword("pwd");
        user.setNickName("nick");
        userMapper.add(user);
    }

    @Test
    public void testSearch(){
        User user = new User();
        user.setUsername("name");
        List<User> select = userMapper.select(user);
        for (User user1 : select) {
            System.out.println(ToStringBuilder.reflectionToString(user1));
        }
    }

    @Test
    public void testUpdate(){
        User user = new User();
        user.setId(1);
        user.setNickName("nickName");
        userMapper.update(user);
    }

    @Test
    public void testSelectById(){
        Optional<User> user = userMapper.selectById(1);
        System.out.println(user.get());
    }

    @Test
    public void testDel(){
        User user = new User();
        user.setUsername("1");
        user.setUsername("name");
        user.setPassword("pwd");
        userMapper.del(user);
    }
}
