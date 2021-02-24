package com.hzoom.game.test;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@SpringBootTest(classes = {TestMockBean.class,TestSpyBean.class})
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)//必须有这个注解，要不然@SpyBean和@MockBean标记的类会为null
@PowerMockIgnore({"org.springframework.*","javax.*","org.mockito.*"})
@PrepareForTest(TestSpyBean.class)
/**
 * 当需要mock final方法的时候，必须加注解@PrepareForTest和@RunWith。注解@PrepareForTest里写的类是final方法所在的类。
 *
 *
 * 当需要mock静态方法的时候，必须加注解@PrepareForTest和@RunWith。注解@PrepareForTest里写的类是静态方法所在的类。
 *
 *
 * 当需要mock私有方法的时候, 只是需要加注解@PrepareForTest，注解里写的类是私有方法所在的类
 *
 *
 * 当需要mock系统类的静态方法的时候，必须加注解@PrepareForTest和@RunWith。注解里写的类是需要调用系统方法所在的类
 */
public class SpringBeanTest extends AbstractTestNGSpringContextTests {
    @SpyBean
    private TestSpyBean testSpyBean;//注入要测试的类,使用SpyBean标记
    @MockBean
    private TestMockBean testMockBean; //注入要测试的类，使用MockBean标记
    @BeforeMethod
    public void setUp() {
        Mockito.reset(testSpyBean);
        Mockito.reset(testMockBean);
    }

    @Test
    public void getName() throws Exception {//测试私有方法时，会有检查异常
        String value = "a";
        testSpyBean = PowerMockito.spy(applicationContext.getBean(TestSpyBean.class));
        PowerMockito.doReturn(value).when(testSpyBean,"getName",Mockito.anyInt());
        String name = Whitebox.invokeMethod(testSpyBean, "getName",1);
        assertEquals(name, value);
        PowerMockito.verifyPrivate(testSpyBean).invoke("getName",1);//验证执行了一次
        PowerMockito.verifyPrivate(testSpyBean,Mockito.times(1)).invoke("getName",1);//在times中指定要验证的执行次数
    }
    @Test
    public void testDoAnswer() throws Exception {
        testSpyBean = PowerMockito.spy(applicationContext.getBean(TestSpyBean.class));
        PowerMockito.doAnswer(answer->{
            int value = answer.getArgument(0);
            assertEquals(value, 12);
            return null;
        }).when(testSpyBean,"getName",Mockito.anyInt());
        testSpyBean.calculate(3);//可以用于判断内部方法调用的情况
    }
}
