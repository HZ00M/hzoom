package com.hzoom.game.error;

public enum  ArenaError implements IError{
    SERVER_ERROR(101,"服务器异常"),
    ;

    private int errorCode;
    private String errorDesc;

    ArenaError(int errorCode, String errorDesc) {
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
}
