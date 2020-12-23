package com.hzoom.im.processor;

import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.session.LocalSession;
import com.hzoom.im.session.ServerSession;
import com.hzoom.im.session.SessionManger;
import com.hzoom.im.utils.Print;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class ChatRedirectProcessor implements ServerProcessor<LocalSession,Boolean>{
    @Autowired
    private SessionManger sessionManger;

    @Override
    public ProtoMsg.HeadType support() {
        return ProtoMsg.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public Boolean handle(LocalSession serverSession, ProtoMsg.Message proto) {
        ProtoMsg.MessageRequest messageRequest = proto.getMessageRequest();
        Print.tcfo("chatMsg | from="
                + messageRequest.getFrom()
                + " , to=" + messageRequest.getTo()
                + " , content=" + messageRequest.getContent());
        // 获取接收方的chatID
        String to = messageRequest.getTo();
        List<ServerSession> sessions = sessionManger.getSessionsBy(to);
        if (CollectionUtils.isEmpty(sessions)){
            Print.tcfo("[" + to + "] 不在线，发送失败!");
        }else {
            sessions.forEach(session->{
                session.send(proto);
            });
        }
        return Boolean.TRUE;
    }
}
