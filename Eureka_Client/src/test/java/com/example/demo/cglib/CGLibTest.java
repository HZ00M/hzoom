package com.example.demo.cglib;

public class CGLibTest {

    public static void main(String[] args) {
        CGLibProxy proxy = new CGLibProxy();
        CGLibTest proxyimp = (CGLibTest) proxy.getProxy(CGLibTest.class);
        int result = proxyimp.test();

    }

    public int test(){
        return 1;
    }

}