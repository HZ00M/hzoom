package com.example.jasypt;

import com.example.demo.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

public class ToStringTest {
    @Test
    public void  toStringTest(){
        User user = new User(1L,"2","3");
        System.out.println(user);
        System.out.println(ToStringBuilder.reflectionToString(user));
        System.out.println(ToStringBuilder.reflectionToString(user, ToStringStyle.SHORT_PREFIX_STYLE));
    }
}