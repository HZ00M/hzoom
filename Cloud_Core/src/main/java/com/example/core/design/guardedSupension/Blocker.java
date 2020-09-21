package com.example.core.design.guardedSupension;

import java.util.concurrent.Callable;

public interface Blocker {
    //当保护条件成立时，执行动作否则阻塞当前线程，直到保护条件成立
    <V> V callWithGuard(GuardedAction<V> guardedAction) throws Exception;

    void signal() throws Exception;

    /**
     * 执行指定的stateOperation操作后，决定是否唤醒本blocker
     * 所暂挂的线程中的一个线程
     *
     * @param stateOperation 更改状态的操作，其call方法返回值为true时，该方法才会唤醒被暂挂的线程
     */
    void signalAfter(Callable<Boolean> stateOperation) throws Exception;

    /**
     * 执行指定的stateOperation操作后，决定是否唤醒本blocker
     * 所暂挂的线程中的所有线程
     *
     * @param stateOperation 更改状态的操作，其call方法返回值为true时，该方法才会唤醒被暂挂的线程
     */
    void broadcastAfter(Callable<Boolean> stateOperation) throws Exception;

}
