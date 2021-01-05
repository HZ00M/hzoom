package com.hzoom.game.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Player")
@Data
public class Player {
    @Id
    private Long playerId;
    private String nickName;
    private Integer level;
    private Long lastLoginTime;
    private Long createTime;

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", nickName='" + nickName + '\'' +
                ", level=" + level +
                ", lastLoginTime=" + lastLoginTime +
                ", createTime=" + createTime +
                '}';
    }
}
