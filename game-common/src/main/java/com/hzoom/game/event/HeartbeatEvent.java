package com.hzoom.game.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HeartbeatEvent extends ApplicationEvent {
    private final Object state;
    public HeartbeatEvent(Object source, Object state) {
        super(source);
        this.state = state;
    }
}
