package com.hzoom.game.context;

import com.hzoom.game.channel.AbstractGameChannelHandlerContext;

public class UserEventContext<T> {

    private T dataManager;
    private AbstractGameChannelHandlerContext ctx;


    public UserEventContext(T dataManager, AbstractGameChannelHandlerContext ctx) {
        super();
        this.dataManager= dataManager;
        this.ctx = ctx;
    }

    public T getDataManager() {
        return dataManager;
    }

    public AbstractGameChannelHandlerContext getCtx() {
        return ctx;
    }


}