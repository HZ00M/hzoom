package com.hzoom.game.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Document("UserAccount")
@Data
public class UserAccount {
    @Id
    private String openId;
    private Long userId;
    private Date createTime;
    private String registerIp;
    private String lastLoginIp;
    private Map<String, ZonePlayerInfo> zonePlayerInfo = new HashMap<>();

    @Data
    public static class ZonePlayerInfo {
        private long playerId;//此区内的角色Id
        private long lastEnterTime;//最近一次进入此区的时间

        public ZonePlayerInfo(Long playerId, long lastEnterTime) {
            this.playerId = playerId;
            this.lastEnterTime = lastEnterTime;
        }

        @Override
        public String toString() {
            return "ZonePlayerInfo{" +
                    "playerId=" + playerId +
                    ", lastEnterTime=" + lastEnterTime +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "openId='" + openId + '\'' +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", registerIp='" + registerIp + '\'' +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", zonePlayerInfo=" + zonePlayerInfo +
                '}';
    }
}
