package com.hzoom.im.command;

import java.util.function.Supplier;

public interface Command<T> {
    void exec(T t);

    Type getKey();

    enum Type{
        CLIENT,CHAT,LOGIN,LOGOUT
    }
}
