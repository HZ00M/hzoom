package com.hzoom.im.sender;

import com.hzoom.im.builder.LoginMsgBuilder;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.protoBuilder.MsgBuilder;
import com.hzoom.im.utils.Print;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginSender extends Sender{
    public void sendLoginMsg() {
        if (!isConnected()) {
            log.info("还没有建立连接!");
            return;
        }
        Print.tcfo("发送登录消息");
        ProtoMsg.Message message =
                LoginMsgBuilder.buildLoginMsg(getUser(), getSession());
        super.sendMsg(message);
    }
}
