package com.example.demo.datasource;

public enum DataSourceEnum {
    CLOUD0, CLOUD1;

    public String withType(Type type) {
        return this + "_" + type;
    }

    public enum Type {
        WRITE,READ,AUTO;
    }
}
