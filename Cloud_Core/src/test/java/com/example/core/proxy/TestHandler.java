package com.example.core.proxy;


import java.lang.reflect.Method;

public class TestHandler implements ProxyInvocationHandler {

    private TargetInterface targetInterface;

    public TestHandler(TargetInterface targetInterface) {
        this.targetInterface = targetInterface;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            method.invoke(targetInterface, args);
            System.out.println("代理方法结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
