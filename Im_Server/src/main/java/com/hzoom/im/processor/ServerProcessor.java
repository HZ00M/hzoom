package com.hzoom.im.processor;

import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.ServerSession;

public interface ServerProcessor<T extends ServerSession> {
    ProtoMsg.HeadType support();

    Boolean handle(T serverSession, ProtoMsg.Message proto);
}
