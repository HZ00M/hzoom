package com.hzoom.game.exception;

import lombok.Getter;

@Getter
public enum  WebGateError {
    UNKNOWN(-2, "网关服务器未知道异常"),
    ;
    private int errorCode;
    private String errorDesc;

    private WebGateError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    @Override
    public String toString() {
        return "errorCode:" + errorCode + "; errorMsg:" + this.errorDesc;
    }
}
