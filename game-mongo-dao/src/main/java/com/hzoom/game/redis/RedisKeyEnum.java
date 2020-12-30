package com.hzoom.game.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;

public enum RedisKeyEnum {
    USER_INFO(Duration.ofDays(7).getSeconds()),
    USER_ID_INCR(RedisKeyEnum.UNLIMIT),
    PLAYER_INFO(Duration.ofDays(7).getSeconds()),
    PLAYER_ID_INCR(RedisKeyEnum.UNLIMIT),
    ;

    RedisKeyEnum(Long timeOutSecond) {
        this.timeOutSecond = timeOutSecond;
    }

    private Long timeOutSecond;
    private static final long UNLIMIT = -1L;

    public String getKey(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + ":" + id;
    }

    public String getKey(){
        return this.name();
    }

    public Integer getTimeOutSecond(){
        return timeOutSecond.intValue();
    }
}
