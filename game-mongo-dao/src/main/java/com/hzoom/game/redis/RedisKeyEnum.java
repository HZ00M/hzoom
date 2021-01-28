package com.hzoom.game.redis;

import java.time.Duration;

public enum RedisKeyEnum {
    USER_INFO(Duration.ofDays(7).getSeconds(),"用户信息"),
    USER_ID_INCR(RedisKeyEnum.LIMITLESS,"UserId 自增key"),
    PLAYER_INFO(Duration.ofDays(7).getSeconds(),"玩家信息"),
    PLAYER_ID_INCR(RedisKeyEnum.LIMITLESS,"PlayerId 自增key"),
    PLAYER_NICKNAME(RedisKeyEnum.LIMITLESS,"用户昵称唯一"),
    ARENA(Duration.ofDays(7).getSeconds(),"竞技场信息")
    ;

    RedisKeyEnum(Long timeOutSecond,String desc) {
        this.timeOutSecond = timeOutSecond;
        this.desc = desc;
    }

    private Long timeOutSecond;
    private String desc;
    private static final long LIMITLESS = -1L;

    public String getKey(String ... ids) {
        StringBuilder builder = new StringBuilder(this.name());
        for (String id : ids) {
            builder.append(":").append(id);
        }
        return builder.toString();
    }

    public Integer getTimeOutSecond(){
        return timeOutSecond.intValue();
    }
}
