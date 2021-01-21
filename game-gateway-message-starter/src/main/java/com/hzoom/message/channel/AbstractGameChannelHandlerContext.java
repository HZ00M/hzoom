package com.hzoom.message.channel;

import com.hzoom.game.message.message.IMessage;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGameChannelHandlerContext {
    volatile AbstractGameChannelHandlerContext next;
    volatile AbstractGameChannelHandlerContext prev;
    private final boolean inbound;
    private final boolean outbound;
    private final GameChannelPipeline pipeline;
    final EventExecutor executor;
    private String name;

    public AbstractGameChannelHandlerContext(GameChannelPipeline pipeline, EventExecutor executor, String name, boolean inbound, boolean outbound) {

        this.name = ObjectUtil.checkNotNull(name, "name");
        this.pipeline = pipeline;
        this.executor = executor;
        this.inbound = inbound;
        this.outbound = outbound;

    }

    public String name() {
        return name;
    }

    private AbstractGameChannelHandlerContext findContextInbound() {
        AbstractGameChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while (!ctx.inbound);
        return ctx;
    }

    private AbstractGameChannelHandlerContext findContextOutbound() {
        AbstractGameChannelHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while (!ctx.outbound);
        return ctx;
    }

    public GameChannel gameChannel() {
        return pipeline.gameChannel();
    }

    public GameChannelPipeline pipeline() {
        return pipeline;
    }

    public EventExecutor executor() {
        if (executor == null) {
            return gameChannel().executor();
        } else {
            return executor;
        }
    }

    public AbstractGameChannelHandlerContext fireChannelInactive() {
        invokeChannelInactive(findContextInbound());
        return this;
    }

    static void invokeChannelInactive(final AbstractGameChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelInactive();
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelInactive();
                }
            });
        }
    }

    private void invokeChannelInactive() {
        try {
            ((GameChannelInboundHandler) handler()).channelInactive(this);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    }

    public AbstractGameChannelHandlerContext fireExceptionCaught(final Throwable cause) {
        invokeExceptionCaught(next, cause);
        return this;
    }

    static void invokeExceptionCaught(final AbstractGameChannelHandlerContext next, final Throwable cause) {
        ObjectUtil.checkNotNull(cause, "cause");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeExceptionCaught(cause);
        } else {
            try {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        next.invokeExceptionCaught(cause);
                    }
                });
            } catch (Throwable t) {
                if (log.isWarnEnabled()) {
                    log.warn("Failed to submit an exceptionCaught() event.", t);
                    log.warn("The exceptionCaught() event that was failed to submit was:", cause);
                }
            }
        }
    }

    private void invokeExceptionCaught(final Throwable cause) {
        try {
            handler().exceptionCaught(this, cause);
        } catch (Throwable error) {
            if (log.isDebugEnabled()) {
                log.debug("An exception {}" + "was thrown by a user handler's exceptionCaught() " + "method while handling the following exception:", ThrowableUtil.stackTraceToString(error), cause);
            } else if (log.isWarnEnabled()) {
                log.warn("An exception '{}' [enable DEBUG level for full stacktrace] " + "was thrown by a user handler's exceptionCaught() " + "method while handling the following exception:", error, cause);
            }
        }

    }

    public AbstractGameChannelHandlerContext fireChannelRegistered(long playerId, GameChannelPromise promise) {
        invokeChannelRegistered(findContextInbound(), playerId, promise);
        return this;
    }

    static void invokeChannelRegistered(final AbstractGameChannelHandlerContext next, long playerId, GameChannelPromise promise) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRegistered(playerId, promise);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelRegistered(playerId, promise);
                }
            });
        }
    }

    private void invokeChannelRegistered(long playerId, GameChannelPromise promise) {

        try {
            ((GameChannelInboundHandler) handler()).channelRegister(this, playerId, promise);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    }

    public AbstractGameChannelHandlerContext fireUserEventTriggered(final Object event, Promise<Object> promise) {
        invokeUserEventTriggered(findContextInbound(), event, promise);
        return this;
    }

    static void invokeUserEventTriggered(final AbstractGameChannelHandlerContext next, final Object event, Promise<Object> promise) {
        ObjectUtil.checkNotNull(event, "event");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeUserEventTriggered(event, promise);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeUserEventTriggered(event, promise);
                }
            });
        }
    }

    private void invokeUserEventTriggered(Object event, Promise<Object> promise) {
        try {
            ((GameChannelInboundHandler) handler()).userEventTriggered(this, event, promise);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    }

    public AbstractGameChannelHandlerContext fireChannelRead(final Object msg) {
        invokeChannelRead(findContextInbound(), msg);
        return this;
    }

    static void invokeChannelRead(final AbstractGameChannelHandlerContext next, final Object msg) {
        ObjectUtil.checkNotNull(msg, "msg");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRead(msg);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelRead(msg);
                }
            });
        }
    }

    private void invokeChannelRead(Object msg) {
        try {
            ((GameChannelInboundHandler) handler()).channelRead(this, msg);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }

    }
    public AbstractGameChannelHandlerContext fireChannelReadRPCRequest(final IMessage msg) {
        invokeChannelReadRPCRequest(findContextInbound(), msg);
        return this;
    }
    static void invokeChannelReadRPCRequest(final AbstractGameChannelHandlerContext next, final IMessage msg) {
        ObjectUtil.checkNotNull(msg, "msg");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelReadRPCRequest(msg);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeChannelReadRPCRequest(msg);
                }
            });
        }
    }
    private void invokeChannelReadRPCRequest(IMessage msg) {
        try {
            ((GameChannelInboundHandler) handler()).channelReadRPCRequest(this, msg);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }

    }

    private void notifyHandlerException(Throwable cause) {
        if (inExceptionCaught(cause)) {
            if (log.isWarnEnabled()) {
                log.warn("An exception was thrown by a user handler " + "while handling an exceptionCaught event", cause);
            }
            return;
        }

        invokeExceptionCaught(cause);
    }

    private static boolean inExceptionCaught(Throwable cause) {
        do {
            StackTraceElement[] trace = cause.getStackTrace();
            if (trace != null) {
                for (StackTraceElement t : trace) {
                    if (t == null) {
                        break;
                    }
                    if ("exceptionCaught".equals(t.getMethodName())) {
                        return true;
                    }
                }
            }

            cause = cause.getCause();
        } while (cause != null);

        return false;
    }

    public GameChannelFuture writeAndFlush(IMessage msg) {
        return writeAndFlush(msg, newPromise());
    }

    public GameChannelPromise newPromise() {
        return new DefaultGameChannelPromise(gameChannel(), this.executor());
    }



    public GameChannelFuture writeAndFlush(IMessage msg, GameChannelPromise promise) {
        AbstractGameChannelHandlerContext next = findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeWrite(msg, promise);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeWrite(msg, promise);
                }
            });
        }
        return promise;
    }

    private void invokeWrite(IMessage msg, GameChannelPromise promise) {
        try {
            ((GameChannelOutboundHandler) handler()).writeAndFlush(this, msg, promise);
        } catch (Throwable t) {
            notifyOutboundHandlerException(t, promise);
        }
    }


    public void writeRPCMessage(IMessage msg, Promise<IMessage> promise) {
        AbstractGameChannelHandlerContext next = findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeWriteRPCMessage(msg, promise);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    next.invokeWriteRPCMessage(msg, promise);
                }
            });
        }
    }

    private void invokeWriteRPCMessage(IMessage msg, Promise<IMessage> callback) {
        try {
            ((GameChannelOutboundHandler) handler()).writeRPCMessage(this, msg, callback);
        } catch (Throwable t) {
            notifyOutboundHandlerException(t, callback);
        }
    }

    private static void notifyOutboundHandlerException(Throwable cause, Promise<?> promise) {
        PromiseNotificationUtil.tryFailure(promise, cause, null);

    }

    public GameChannelFuture close() {
        return this.close(new DefaultGameChannelPromise(this.gameChannel()));
    }

    public GameChannelFuture close(final GameChannelPromise promise) {


        final AbstractGameChannelHandlerContext next = findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeClose(promise);
        } else {
            safeExecute(executor, new Runnable() {
                @Override
                public void run() {
                    next.invokeClose(promise);
                }
            }, promise, null);
        }

        return promise;
    }

    private void invokeClose(GameChannelPromise promise) {

        try {
            ((GameChannelOutboundHandler) handler()).close(this, promise);
        } catch (Throwable t) {
            notifyOutboundHandlerException(t, promise);
        }

    }

    private static boolean safeExecute(EventExecutor executor, Runnable runnable, GameChannelPromise promise, Object msg) {
        try {
            executor.execute(runnable);
            return true;
        } catch (Throwable cause) {
            try {
                promise.setFailure(cause);
            } finally {
                if (msg != null) {
                    ReferenceCountUtil.release(msg);
                }
            }
            return false;
        }
    }

    public abstract GameChannelHandler handler();


}
