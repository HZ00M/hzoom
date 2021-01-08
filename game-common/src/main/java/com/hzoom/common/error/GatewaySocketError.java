package com.hzoom.common.error;

public enum GatewaySocketError implements IError {
    REPEAT_CONNECT(103,"重复连接");
    ;

    private int errorCode;
    private String errorDesc;

    GatewaySocketError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return errorDesc;
    }

    @Override
    public String toString() {
        return print();
    }
}
