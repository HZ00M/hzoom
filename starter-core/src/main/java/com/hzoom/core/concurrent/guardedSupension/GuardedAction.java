package com.hzoom.core.concurrent.guardedSupension;

import java.util.concurrent.Callable;

//带保护条件的目标动作
public abstract class GuardedAction<V> implements Callable<V> {
    protected final Predicate guard;

    public GuardedAction(Predicate guard) {
        this.guard = guard;
    }
}
