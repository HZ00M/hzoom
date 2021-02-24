package com.hzoom.game.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzoom.game.CenterServerApplication;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

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

    @Test
    public void testGetSpecialValue() {
        //都指定返回值
        Mockito.doReturn(30).when(tUserService).count();
        Mockito.when(mockBean.count()).thenReturn(100);
        int count = tUserService.count();
        assertEquals(count, 30);
        int count1 = mockBean.count();
        assertEquals(count1, 100);

        Mockito.verify(mockBean).count();//默认验证调用了一次
        for (int i = 0; i < 4; i++) {
            mockBean.count();
        }
        Mockito.verify(mockBean, Mockito.times(5)).count();//还可以指定验证调用了多少次
    }

    @DataProvider
    public Object[][] data() {
        Object[][] data = {
                {"1", "100", "1"},
                {"2", "200", "1"},
                {"3", "300", "1"},
                {"4", "500", "1"}
        };
        return data;
    }

    @Test(groups = "testdata",dataProvider = "data")
    public void testDataProdiver(String username, String password, String nickname) {
        TUser user = new TUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        spyBean.save(user);
    }
}
