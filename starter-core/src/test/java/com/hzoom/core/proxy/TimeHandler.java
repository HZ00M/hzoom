package com.hzoom.core.proxy;


import java.lang.reflect.Method;
import java.util.Date;

public class TimeHandler implements ProxyInvocationHandler{

    TargetInterface targetInterface;

    public TimeHandler(TargetInterface targetInterface) {
        this.targetInterface = targetInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            System.out.println("开始时间"+new Date());
            method.invoke(targetInterface,args);
            System.out.println("结束时间"+new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
