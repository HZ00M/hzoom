package com.example.core.design.activeObject;

import java.util.concurrent.Future;

public class SampleActiveObjectImpl implements SampleActiveObject {
    @Override
    public Future<String> getA() {
        System.out.println("getA");
        return null;
    }

    @Override
    public String getB() {
        System.out.println("getB");
        return null;
    }

    public Future<String> doGetA() {
        System.out.println("doGetA");
        return null;
    }
}
