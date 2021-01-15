package com.hzoom.game;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@WebAppConfiguration
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class)//不加的话拿不到spring里面的东西
public class BaseTest extends AbstractTestNGSpringContextTests {
    MockMvc mockMvc = null;
}
