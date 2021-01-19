package com.hzoom.game.event;

import com.hzoom.game.entity.manager.PlayerManager;
import org.springframework.context.ApplicationEvent;

public class ConsumeDiamond extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    private int diamond;
    private PlayerManager playerManager;
    public ConsumeDiamond(Object source,int diamond,PlayerManager playerManager) {
        super(source);
        this.diamond = diamond;
        this.playerManager = playerManager;
    }
    public int getDiamond() {
        return diamond;
    }
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    

}
