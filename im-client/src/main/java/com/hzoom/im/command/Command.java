package com.hzoom.im.command;

public interface Command<T> {
    void exec(T t);

    Type getKey();

    enum Type{
        CLIENT,CHAT,LOGIN,LOGOUT
    }
}
