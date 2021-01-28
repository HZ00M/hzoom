package com.hzoom.im.builder;

import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.ClientSession;

public class HeartBeatMsgBuilder extends Builder{
    private final UserDTO user;

    public HeartBeatMsgBuilder(UserDTO user, ClientSession session) {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message buildMsg() {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.MessageHeartBeat.Builder lb =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUserId());
        return message.toBuilder().setHeartBeat(lb).build();
    }
}
