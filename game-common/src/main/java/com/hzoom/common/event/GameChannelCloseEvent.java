package com.hzoom.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameChannelCloseEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private long playerId;

    public GameChannelCloseEvent(Object source, long playerId) {
        super(source);
        this.playerId = playerId;
    }
}
