package com.hzoom.message.rpc;

import com.hzoom.message.channel.AbstractGameChannelHandlerContext;
import com.hzoom.game.message.dispatcher.IChannelContext;
import com.hzoom.game.message.message.IMessage;

public class RPCEventContext<T>  implements IChannelContext {
    private IMessage request;
    private T data;//这个用于存储缓存的数据，因为不同的服务的数据结构是不同的，所以这里使用泛型
    private AbstractGameChannelHandlerContext ctx;

    public RPCEventContext(T data,IMessage request, AbstractGameChannelHandlerContext ctx) {
        super();
        this.request = request;
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public void sendMessage(IMessage response) {
        wrapResponseMessage(response);
        ctx.writeRPCMessage(response, null);
    }


    private void wrapResponseMessage(IMessage response) {
        IMessage.Header responseHeader = response.getHeader();
        IMessage.Header requestHeader = request.getHeader();
        IMessage.MessageType messageType = responseHeader.getMessageType();
        if(messageType != IMessage.MessageType.RPC_RESPONSE) {
            throw new IllegalArgumentException(response.getClass().getName() + " 参数类型不对，不是RPC的响应数据对象");
        }
        responseHeader.setToServerId(requestHeader.getFromServerId());
        responseHeader.setFromServerId(requestHeader.getToServerId());
        responseHeader.setClientSeqId(requestHeader.getClientSeqId());
        responseHeader.setClientSendTime(requestHeader.getClientSendTime());
        responseHeader.setPlayerId(requestHeader.getPlayerId());
        responseHeader.setServerSendTime(System.currentTimeMillis());

    }

    public T getData() {
        return data;
    }

    @Override
    public <E> E getRequest() {
        return (E) this.request;
    }

    @Override
    public long getPlayerId() {
        return this.request.getHeader().getPlayerId();
    }
}
