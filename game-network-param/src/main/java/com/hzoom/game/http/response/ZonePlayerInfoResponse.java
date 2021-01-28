package com.hzoom.game.http.response;

import lombok.Data;

@Data
public class ZonePlayerInfoResponse {
    private long playerId;//此区内的角色Id
    private long lastEnterTime;//最近一次进入此区的时间

    public ZonePlayerInfoResponse() {}
    public ZonePlayerInfoResponse(long playerId, long lastEnterTime) {
        super();
        this.playerId = playerId;
        this.lastEnterTime = lastEnterTime;
    }
}
