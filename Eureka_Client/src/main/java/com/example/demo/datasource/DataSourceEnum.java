package com.example.demo.datasource;

public enum DataSourceEnum {
    CLOUD, CLOUD1;

    public String withType(Type type) {
        return this + "" + type;
    }

    public enum Type {
        WRITE,READ,AUTO;
    }
}
