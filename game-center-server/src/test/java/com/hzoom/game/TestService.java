package com.hzoom.game;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzoom.game.common.entity.TUser;
import com.hzoom.game.common.mapper.TUserMapper;
import com.hzoom.game.common.service.TUserService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {CenterServerApplication.class})
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)//必须有这个注解，要不然@SpyBean和@MockBean标记的类会为null
public class TestService extends AbstractTestNGSpringContextTests {
    @SpyBean
    private TUserService spyBean;
    @Mock
    private TUserService mockBean;
    @Autowired
    private TUserService tUserService;
    @Autowired
    private TUserMapper tUserMapper;

    @BeforeMethod
    public void setUp() {
        Mockito.reset(spyBean);
        Mockito.reset(mockBean);
    }

    @AfterMethod
    public void reset() {
        Mockito.reset(spyBean);
        Mockito.reset(mockBean);
    }

    @Test
    public void insert() {
        TUser user = new TUser();
        user.setNickname("nickName");
        user.setPassword("123");
        user.setUsername("userName");
        tUserMapper.inserts(user);
    }

    @Test
    public void test() {
        int count = spyBean.count();
        int count1 = mockBean.count();
        assertEquals(count1, 0);
        int count2 = tUserService.count();
        List<TUser> list = spyBean.list();
        List<TUser> list1 = mockBean.list();
        List<TUser> list2 = tUserService.list();
    }

    @Test
    public void page() {
        Map<String, Object> map = new HashMap<>();

        QueryWrapper<TUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        Page<TUser> page = new Page<>(1, 1);  // 查询第1页，每页返回1条
        Page<TUser> list = spyBean.page(page, queryWrapper);
        assertEquals(list.getSize(), 1);
    }
}
