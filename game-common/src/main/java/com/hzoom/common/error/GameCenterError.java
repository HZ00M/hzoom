package com.hzoom.common.error;

public enum GameCenterError implements IError {
    UNKNOWN(-1, "用户中心服务未知异常"),
    OPENID_IS_EMPTY(1, "openId为空"),
    OPENID_LEN_ERROR(2, "openId长度不对"),
    SDK_TOKEN_IS_EMPTY(3, "SDK token错误"),
    SDK_TOKEN_LEN_ERROR(4, "sdk token 长度不对"),
    NICKNAME_EXIST(5, "昵称已存在"),
    ZONE_ID_IS_EMPTY(6, "zoneId为空"),
    NICKNAME_IS_EMPTY(7, "昵称为空"),
    NICKNAME_LEN_ERROR(8, "昵称长度不对"),
    TOKEN_FAILED(9, "token错误"),
    NO_GAME_GATEWAY_INFO(10, "没有网关信息，无法连接游戏"),
    ;

    private int errorCode;
    private String errorDesc;

    GameCenterError(int errorCode, String errorDesc) {
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
