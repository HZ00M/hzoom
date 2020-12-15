package com.hzoom.im.builder;

import com.hzoom.im.bean.ChatMsg;
import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.session.ClientSession;
import com.hzoom.im.proto.ProtoMsg;

public class ChatMsgBuilder extends Builder{
    private ChatMsg chatMsg;
    private UserDTO user;

    public ChatMsgBuilder(ChatMsg chatMsg, UserDTO user, ClientSession session) {
        super(ProtoMsg.HeadType.MESSAGE_REQUEST, session);
        this.chatMsg = chatMsg;
        this.user = user;
    }

    public ProtoMsg.Message build() {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.MessageRequest.Builder builder = ProtoMsg.MessageRequest.newBuilder();
        chatMsg.fillMsg(builder);
        return message.toBuilder().setMessageRequest(builder).build();
    }

    public static ProtoMsg.Message buildChatMsg(ChatMsg chatMsg, UserDTO user, ClientSession session) {
        ChatMsgBuilder builder = new ChatMsgBuilder(chatMsg, user, session);
        return builder.build();
    }
}
