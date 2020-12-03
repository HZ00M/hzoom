package com.hzoom.im.builder;

import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.clientSession.ClientSession;
import com.hzoom.im.proto.ProtoMsg;

public class LogoutMsgBuilder extends Builder{
    private final UserDTO user;
    public LogoutMsgBuilder(UserDTO user, ClientSession session) {
        super(ProtoMsg.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }

    public ProtoMsg.Message build() {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.LogoutRequest.Builder lb =
                ProtoMsg.LogoutRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setUid(user.getUserId());
        return message.toBuilder().setLogoutRequest(lb).build();
    }

    public static ProtoMsg.Message buildLogoutMsg(
            UserDTO user,
            ClientSession session) {
        LogoutMsgBuilder builder = new LogoutMsgBuilder(user, session);
        return builder.build();
    }
}
