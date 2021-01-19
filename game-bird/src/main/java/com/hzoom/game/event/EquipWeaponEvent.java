package com.hzoom.game.event;

import org.springframework.context.ApplicationEvent;

public class EquipWeaponEvent extends ApplicationEvent {
    public EquipWeaponEvent(Object source) {
        super(source);
    }
    private String heroId;
    private String weaponId;
    public String getHeroId() {
        return heroId;
    }
    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }
    public String getWeaponId() {
        return weaponId;
    }
    public void setWeaponId(String weaponId) {
        this.weaponId = weaponId;
    }
    
    
}
