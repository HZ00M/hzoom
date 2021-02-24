package com.hzoom.game.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestSpyBean {
    @Autowired
    private TestMockBean testMockBean;

    public int getValue() {
        return 3;
    }

    public int getMockBeanLevel() {
        return testMockBean.getValue();
    }
    private String getName(int a) {
        return "a";
    }

    public int getValue(int type) {
        int value = 0;
        switch (type) {
            case 1:
                value = 100;
                break;
            case 2:
                value = 200;
                break;
            case 3:
                value = 300;
                break;
            default:
                value = 500;
                break;
        }
        return value;
    }

    public void saveData(String value) {
        if (value != null && !value.isEmpty()) {
            testMockBean.saveToRedis(value);
        }
    }

    public static int queryValue() {
        return 3;
    }

    public void calculate(int a) {
        int value = (a + 3) *2;
        this.getName(value);
    }
}