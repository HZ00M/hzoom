package com.hzoom.im.sender;

import com.hzoom.im.builder.LoginMsgBuilder;
import com.hzoom.im.builder.LogoutMsgBuilder;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.utils.Print;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutSender extends Sender{
    public void sendLogoutMsg() {
        if (!isConnected()) {
            log.info("还没有建立连接!");
            return;
        }
        Print.tcfo("发送退出消息");
        ProtoMsg.Message message =
                LogoutMsgBuilder.buildLogoutMsg(getUser(), getSession());
        super.sendMsg(message);
    }
}
