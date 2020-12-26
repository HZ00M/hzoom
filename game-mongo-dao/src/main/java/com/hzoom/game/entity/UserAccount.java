package com.hzoom.game.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "UserAccount")
@Data
public class UserAccount {
    @Id
    private String openId;
    private Long userId;
    private Date createTime;
    private String registerIp;
    private String lastLoginIp;

    @Override
    public String toString() {
        return "UserAccount{" +
                "openId='" + openId + '\'' +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", registerIp='" + registerIp + '\'' +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                '}';
    }
}
