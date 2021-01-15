package com.hzoom.game.context;

import com.hzoom.game.channel.AbstractGameChannelHandlerContext;
import com.hzoom.game.message.dispatcher.IChannelContext;
import com.hzoom.game.message.message.IMessage;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class GatewayMessageContext<T> implements IChannelContext {
    private IMessage requestMessage;
    private AbstractGameChannelHandlerContext ctx;
    private T dataManager;

    public GatewayMessageContext(T dataManager, IMessage requestMessage, AbstractGameChannelHandlerContext ctx) {
        this.requestMessage = requestMessage;
        this.ctx = ctx;
        this.dataManager = dataManager;
    }

    public T getDataManager(){
        return dataManager;
    }

    @Override
    public void sendMessage(IMessage response) {
        if (response != null) {
            wrapResponseMessage(response);
            ctx.writeAndFlush(response);
        }
    }

    private void wrapResponseMessage(IMessage response) {
        IMessage.Header responseHeader = response.getHeader();
        IMessage.Header requestHeader = requestMessage.getHeader();
        responseHeader.setClientSendTime(requestHeader.getClientSendTime());
        responseHeader.setClientSeqId(requestHeader.getClientSeqId());
        responseHeader.setPlayerId(requestHeader.getPlayerId());
        responseHeader.setServerSendTime(System.currentTimeMillis());
        responseHeader.setToServerId(requestHeader.getFromServerId());
        responseHeader.setFromServerId(requestHeader.getToServerId());
        responseHeader.setVersion(requestHeader.getVersion());
    }

    public void broadcastMessage(IMessage message) {
        if(message != null) {
            ctx.gameChannel().getEventDispatchService().broadcastMessage(message);
        }
    }
    public void broadcastMessage(IMessage message,long...playerIds) {
        ctx.gameChannel().getEventDispatchService().broadcastMessage(message,playerIds);
    }

    public Future<IMessage> sendRPCMessage(IMessage rpcRequest, Promise<IMessage> callback) {
        if (rpcRequest != null) {
            rpcRequest.getHeader().setPlayerId(ctx.gameChannel().getPlayerId());
            ctx.writeRPCMessage(rpcRequest, callback);
        } else {
            throw new NullPointerException("RPC消息不能为空");
        }
        return callback;
    }

    public void sendRPCMessage(IMessage rpcRequest) {
        if (rpcRequest != null) {
            ctx.writeRPCMessage(rpcRequest, null);
        } else {
            throw new NullPointerException("RPC消息不能为空");
        }
    }

    public Future<Object> sendUserEvent(Object event, Promise<Object> promise, long playerId) {
        ctx.gameChannel().getEventDispatchService().fireUserEvent(playerId, event, promise);
        return promise;
    }

    public <E> DefaultPromise<E> newPromise() {
        return new DefaultPromise<>(ctx.executor());
    }

    @Override
    public <E> E getRequest() {
        return (E) this.requestMessage;
    }

    @Override
    public long getPlayerId() {
        return this.requestMessage.getHeader().getPlayerId();
    }
}
