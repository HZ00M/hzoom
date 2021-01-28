package com.hzoom.game.error;

public enum GatewaySocketError implements IError {
    REPEAT_CONNECT(103,"重复连接"),
    NOT_INSTANCE(104,"获取不到服务器实例")
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
