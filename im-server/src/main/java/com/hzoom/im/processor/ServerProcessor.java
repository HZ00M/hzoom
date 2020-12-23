package com.hzoom.im.processor;

import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.ServerSession;

public interface ServerProcessor<T extends ServerSession,R> {
    ProtoMsg.HeadType support();

    R handle(T serverSession, ProtoMsg.Message proto);
}
