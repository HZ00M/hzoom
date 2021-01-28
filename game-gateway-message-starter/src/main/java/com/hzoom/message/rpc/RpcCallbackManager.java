package com.hzoom.message.rpc;

import com.hzoom.game.error.GameRPCError;
import com.hzoom.game.exception.ErrorException;
import com.hzoom.game.message.common.IMessage;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcCallbackManager {
    private Map<Integer, Promise<IMessage>> callbackMap = new ConcurrentHashMap<>();
    private EventExecutorGroup eventExecutorGroup;
    private int timeout = 30;// 超时时间，30s;

    public RpcCallbackManager(EventExecutorGroup eventExecutorGroup) {
        this.eventExecutorGroup = eventExecutorGroup;
    }

    public void addCallback(Integer seqId, Promise<IMessage> promise) {
        if (promise == null) {
            return;
        }
        callbackMap.put(seqId, promise);
        // 启动一个延时任务，如果到达时间还没有收到返回，超抛出超时异常
        eventExecutorGroup.schedule(() -> {
            Promise<?> value = callbackMap.remove(seqId);
            if (value != null) {
                value.setFailure(ErrorException.newBuilder(GameRPCError.TIME_OUT).build());
            }
        }, timeout, TimeUnit.SECONDS);
    }

    public void callback(IMessage message) {
        int seqId = message.getHeader().getClientSeqId();
        Promise<IMessage> promise = this.callbackMap.remove(seqId);
        if (promise != null) {
            promise.setSuccess(message);
        }
    }
}
