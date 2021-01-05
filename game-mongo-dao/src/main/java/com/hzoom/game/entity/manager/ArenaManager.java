package com.hzoom.game.entity.manager;

import com.hzoom.game.entity.Arena;

public class ArenaManager {
    private Arena arena;

    public ArenaManager(Arena arena) {
        this.arena = arena;
    }

    public void addChallengeTimes(int times) {
        int result = arena.getChallengeTimes() + times;
        arena.setChallengeTimes(result);
    }
}
