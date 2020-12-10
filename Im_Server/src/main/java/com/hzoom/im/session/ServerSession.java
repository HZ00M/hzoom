package com.hzoom.im.session;

import io.netty.channel.ChannelFuture;

public interface ServerSession {
    ChannelFuture send(Object pkg);

    ChannelFuture close();

    String getSessionId();

    boolean isValid();
}
