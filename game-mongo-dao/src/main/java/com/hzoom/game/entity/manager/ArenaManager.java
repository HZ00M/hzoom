package com.hzoom.game.entity.manager;

import com.hzoom.game.entity.Arena;
import lombok.Getter;
import lombok.Setter;

public class ArenaManager {
    @Getter
    @Setter
    private Arena arena;

    public ArenaManager(Arena arena) {
        this.arena = arena;
    }

    public void addChallengeTimes(int times) {
        int result = arena.getChallengeTimes() + times;
        arena.setChallengeTimes(result);
    }
}
