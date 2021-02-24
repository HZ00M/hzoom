package com.hzoom.game.test;

import org.springframework.stereotype.Service;

@Service
public class TestMockBean {

    public int getValue() {
        return 2;
    }
    public void saveToRedis(String value) {
        throw new UnsupportedOperationException();
    }


}
