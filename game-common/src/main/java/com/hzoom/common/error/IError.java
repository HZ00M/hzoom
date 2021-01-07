package com.hzoom.common.error;


public interface IError {
    int getErrorCode();
    String getErrorDesc();

    default String print() {
        StringBuilder msg = new StringBuilder();
        msg.append("errorCode:").append(getErrorCode()).append("; errorMsg:").append(getErrorDesc());
        return msg.toString();
    }
}
