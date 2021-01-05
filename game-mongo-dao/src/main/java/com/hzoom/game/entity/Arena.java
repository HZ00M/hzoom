package com.hzoom.game.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Arena")
@Data
public class Arena {
    @Id
    private long playerId;
    private int challengeTimes;// 当前剩余的挑战次数
}
