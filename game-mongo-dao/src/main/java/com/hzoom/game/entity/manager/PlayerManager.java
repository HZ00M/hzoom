package com.hzoom.game.entity.manager;

import com.hzoom.game.entity.Player;
import lombok.Getter;

@Getter
public class PlayerManager {
    private Player player;

    public PlayerManager(Player player) {
        this.player = player;
    }

    public int addPlayerExp(int exp){
        //添加角色经验，判断是否升级，返回升级后当前最新的等级
        return player.getLevel();
    }
}
