package com.example.core.proxy;

public class TimeTarget implements TargetInterface {
    @Override
    public void targetMethod() {
        System.out.println("时间方法");
    }

}
