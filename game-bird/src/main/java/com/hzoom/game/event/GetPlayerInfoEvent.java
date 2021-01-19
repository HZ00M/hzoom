package com.hzoom.game.event;

public class GetPlayerInfoEvent {
    private Long playerId;
    public GetPlayerInfoEvent(Long playerId) {
        super();
        this.playerId = playerId;
    }
    public Long getPlayerId() {
        return playerId;
    }
}
