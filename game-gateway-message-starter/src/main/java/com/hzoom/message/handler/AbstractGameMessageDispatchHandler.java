package com.hzoom.message.handler;

import com.hzoom.message.channel.AbstractGameChannelHandlerContext;
import com.hzoom.message.channel.GameChannelInboundHandler;
import com.hzoom.message.channel.GameChannelPromise;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.message.context.DispatchUserEventManager;
import com.hzoom.message.context.GatewayMessageContext;
import com.hzoom.message.context.UserEventContext;
import com.hzoom.game.message.DispatchMessageManager;
import com.hzoom.game.message.common.IMessage;
import com.hzoom.message.rpc.DispatchRPCEventManager;
import com.hzoom.message.rpc.RPCEventContext;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;


@Slf4j
public abstract class AbstractGameMessageDispatchHandler<T> implements GameChannelInboundHandler {
    @Getter
    protected long playerId;
    protected DispatchMessageManager dispatchMessageManager;
    protected DispatchRPCEventManager dispatchRPCEventManager;
    protected DispatchUserEventManager dispatchUserEventManager;
    protected ScheduledFuture<?> flushToRedisScheduledFuture;
    protected ScheduledFuture<?> flushToDBScheduledFuture;
    protected ChannelServerProperties serverProperties;

    protected abstract T getDataManager();

    protected abstract void initData(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise initPromise);

    protected abstract Future<Boolean> updateToRedis(Promise<Boolean> promise);

    protected abstract Future<Boolean> updateToDB(Promise<Boolean> promise);

    public AbstractGameMessageDispatchHandler(ApplicationContext context){
        dispatchMessageManager = context.getBean(DispatchMessageManager.class);
        dispatchRPCEventManager = context.getBean(DispatchRPCEventManager.class);
        dispatchUserEventManager = context.getBean(DispatchUserEventManager.class);
        serverProperties = context.getBean(ChannelServerProperties.class);
    }

    @Override
    public void channelRegister(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise) {
        this.playerId = playerId;
        GameChannelPromise initPromise = ctx.newPromise();
        initPromise.addListener((Future<? super Void> f)->{
            fixTimerFlushPlayer(ctx);
            promise.setSuccess();
        });
        initData(ctx,playerId,initPromise);
    }

    @Override
    public void channelInactive(AbstractGameChannelHandlerContext ctx) throws Exception {
        if (flushToDBScheduledFuture!=null){
            flushToDBScheduledFuture.cancel(true);
        }
        if (flushToRedisScheduledFuture!=null){
            flushToRedisScheduledFuture.cancel(true);
        }
        this.updateToRedis0(ctx);
        this.updateToDB0(ctx);
        log.debug("game channel 移除，playerId:{}", getPlayerId());
        ctx.fireChannelInactive();// 向下一个Handler发送channel失效事件
    }

    @Override
    public void channelRead(AbstractGameChannelHandlerContext ctx, Object msg) throws Exception {
        IMessage message = (IMessage)msg;
        T dataManager = getDataManager();
        GatewayMessageContext<T> messageContext = new GatewayMessageContext<>(dataManager,message,ctx);
        dispatchMessageManager.callMethod(messageContext,message);
    }

    @Override
    public void channelReadRPCRequest(AbstractGameChannelHandlerContext ctx, IMessage msg) throws Exception {
        T dataManager = getDataManager();
        RPCEventContext<T> rpcEventContext = new RPCEventContext<>(dataManager,msg,ctx);
        dispatchRPCEventManager.callMethod(rpcEventContext,msg);
    }

    @Override
    public void userEventTriggered(AbstractGameChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
        T dataManager = getDataManager();
        UserEventContext<T> userEventContext = new UserEventContext<>(dataManager,ctx);
        dispatchUserEventManager.callMethod(userEventContext,evt,promise);
    }

    @Override
    public void exceptionCaught(AbstractGameChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private void fixTimerFlushPlayer(AbstractGameChannelHandlerContext ctx){
        int flushRedisDelaySecond = serverProperties.getFlushRedisDelaySecond();
        int flushDBDelaySecond = serverProperties.getFlushDBDelaySecond();
        flushToRedisScheduledFuture = ctx.executor().scheduleWithFixedDelay(()->{
            this.updateToRedis0(ctx);
        },flushRedisDelaySecond,flushRedisDelaySecond, TimeUnit.SECONDS);
        flushToDBScheduledFuture = ctx.executor().scheduleWithFixedDelay(()->{
            this.updateToDB0(ctx);
        },flushDBDelaySecond,flushDBDelaySecond,TimeUnit.SECONDS);
    }

    protected void updateToDB0(AbstractGameChannelHandlerContext ctx){
        long start = System.currentTimeMillis();
        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
        this.updateToDB(promise).addListener((Future<Boolean> f)->{
            if (f.isSuccess()){
                if (log.isDebugEnabled()){
                    long end = System.currentTimeMillis();
                    log.debug("player {} 同步数据到redis成功,耗时:{} ms", getPlayerId(), (end - start));
                }
            }else {
                log.error("player {} 同步数据到Redis失败", getPlayerId());
                // TODO 这个时候应该报警
            }
        });
    }

    private void updateToRedis0(AbstractGameChannelHandlerContext ctx){
        long start = System.currentTimeMillis();
        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
        this.updateToRedis(promise).addListener((Future<Boolean> f)->{
            if (f.isSuccess()){
                if (log.isDebugEnabled()){
                    long end = System.currentTimeMillis();
                    log.debug("player {} 同步数据到redis成功,耗时:{} ms", getPlayerId(), (end - start));
                }
            }else {
                log.error("player {} 同步数据到Redis失败", getPlayerId());
                // TODO 这个时候应该报警
            }
        });
    }
}
