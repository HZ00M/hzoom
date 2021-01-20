package com.hzoom.game;

import lombok.Getter;

@Getter
public enum GatewayMessageTypeEnum {
    ConnectConfirm(1, "连接认证"),
    Heartbeat(2, "心跳消息"),
    ;
    private int messageId;
    private String desc;

    GatewayMessageTypeEnum(int messageId, String desc) {
        this.messageId = messageId;
        this.desc = desc;
    }

}
