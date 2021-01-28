package com.hzoom.im.builder;

import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.ClientSession;

public abstract class Builder {
    protected ProtoMsg.HeadType type;
    private long seqId;
    private ClientSession session;

    public Builder(
            ProtoMsg.HeadType type,
            ClientSession session) {
        this.type = type;
        this.session = session;
    }

    public ProtoMsg.Message buildCommon(long seqId) {
        this.seqId = seqId;
        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }
}
