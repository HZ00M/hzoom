package com.hzoom.game.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;

public enum EnumRedisKey {
    PLAYER_INFO(Duration.ofDays(7).getSeconds()),
    PLAYER_ID_INCR(EnumRedisKey.UNLIMIT),
    ;

    EnumRedisKey(Long timeOutSecond) {
        this.timeOutSecond = timeOutSecond;
    }

    private Long timeOutSecond;
    private static final long UNLIMIT = -1L;

    public String getKey(String seq) {
        if (StringUtils.isEmpty(seq)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + ":" + seq;
    }

    public String getKey(){
        return this.name();
    }

    public Long getTimeOutSecond(){
        return timeOutSecond;
    }
}
