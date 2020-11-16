package com.hzoom.demo.util;

public enum ResultCode {
    error(0,"异常"),
    ok(200,"成功");

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
