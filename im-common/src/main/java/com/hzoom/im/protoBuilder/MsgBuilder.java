package com.hzoom.im.protoBuilder;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.proto.ProtoMsg;
import org.springframework.stereotype.Component;

@Component
public class MsgBuilder {
    public static ProtoMsg.Message buildNotification(String json) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_NOTIFICATION);

        ProtoMsg.MessageNotification.Builder rb =
                ProtoMsg.MessageNotification.newBuilder()
                        .setJson(json);
        mb.setNotification(rb);
        return mb.build();
    }

    public static ProtoMsg.Message buildChatResponse(long seqId, ServerConstants.ResultCodeEnum resultCode) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_RESPONSE)
                .setSequence(seqId);
        ProtoMsg.MessageResponse.Builder rb = ProtoMsg.MessageResponse.newBuilder()
                .setCode(resultCode.getCode())
                .setInfo(resultCode.getDesc())
                .setExpose(1);
        mb.setMessageResponse(rb);
        return mb.build();
    }


    public static ProtoMsg.Message buildLoginResponse(
            ServerConstants.ResultCodeEnum en, long seqId, String sessionId) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.LOGIN_RESPONSE)  //设置消息类型
                .setSequence(seqId)
                .setSessionId(sessionId);  //设置应答流水，与请求对应

        ProtoMsg.LoginResponse.Builder lb = ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setLoginResponse(lb);
        return mb.build();
    }
}
