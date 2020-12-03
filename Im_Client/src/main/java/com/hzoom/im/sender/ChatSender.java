package com.hzoom.im.sender;

import com.hzoom.im.bean.ChatMsg;
import com.hzoom.im.builder.ChatMsgBuilder;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.utils.Print;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatSender extends Sender{
    public void sendTextMsg(String toUserId, String content) {
        ChatMsg chatMsg = new ChatMsg(getUser());
        chatMsg.setContent(content);
        chatMsg.setMsgType(ChatMsg.MsgType.TEXT);
        chatMsg.setTo(toUserId);
        chatMsg.setMsgId(System.currentTimeMillis());
        ProtoMsg.Message message = ChatMsgBuilder.buildChatMsg(chatMsg,getUser(),getSession());
        super.sendMsg(message);
    }

    @Override
    protected void sendSuccess(ProtoMsg.Message message) {
        Print.tcfo("单聊发送成功:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }

    @Override
    protected void sendException(ProtoMsg.Message message) {
        Print.tcfo("单聊发送异常:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }

    @Override
    protected void sendFailed(ProtoMsg.Message message) {
        Print.tcfo("单聊发送失败:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }
}
