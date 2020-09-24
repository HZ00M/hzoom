package com.example.demo.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilterTest {
    @Test
    public void testFilter(){
        Request request = new Request("敏感内容测试");
        Response response = new Response("敏感回复内容");
        FilterChain chain = new FilterChain();
        chain.addFilter(new HtmlFilter()).addFilter(new ExecFilter());
        chain.doFilter(request,response,chain);
        System.out.println(request.getContent());
        System.out.println(response.getContent());
    }
}
