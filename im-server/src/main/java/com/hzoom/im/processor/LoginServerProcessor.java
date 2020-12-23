package com.hzoom.im.processor;

import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.protoBuilder.MsgBuilder;
import com.hzoom.im.session.LocalSession;
import com.hzoom.im.session.ServerSession;
import com.hzoom.im.session.SessionManger;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LoginServerProcessor implements ServerProcessor<LocalSession,Boolean> {
    @Autowired
    MsgBuilder msgBuilder;
    @Autowired
    SessionManger sessionManger;
    @Override
    public ProtoMsg.HeadType support() {
        return ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public Boolean handle(LocalSession serverSession, ProtoMsg.Message proto) {
        ProtoMsg.LoginRequest loginRequest = proto.getLoginRequest();
        long sequence = proto.getSequence();
        UserDTO user = UserDTO.fromMsg(loginRequest);
        boolean isValidUser = checkUser(user);
        if (!isValidUser){
            ServerConstants.ResultCodeEnum resultCode = ServerConstants.ResultCodeEnum.NO_TOKEN;
            ProtoMsg.Message loginResponse = MsgBuilder.buildLoginResponse(resultCode,sequence,"-1");
            serverSession.send(loginResponse);
            serverSession.close();
            return false;
        }
        serverSession.setSessionUser(user);
        serverSession.bind();
        sessionManger.addLocalSession(serverSession);

        /**
         * 通知客户端：登录成功
         */
        ServerConstants.ResultCodeEnum resultCode = ServerConstants.ResultCodeEnum.SUCCESS;
        ProtoMsg.Message response = MsgBuilder.buildLoginResponse(resultCode,sequence,serverSession.getSessionId());
        serverSession.send(response);
        return true;
    }

    protected String getKey(Channel ch) {
        return ch.attr(LocalSession.USER_ID_KEY).get();
    }

    protected void setKey(Channel ch, String key) {
        ch.attr(LocalSession.USER_ID_KEY).set(key);
    }

    protected void checkAuth(Channel ch) throws Exception {
        if (null == getKey(ch)) {
            throw new Exception("此用户，没有登录成功");
        }
    }

    private boolean checkUser(UserDTO user) {

        //校验用户,比较耗时的操作,需要100 ms以上的时间
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验
        List<ServerSession> sessions = sessionManger.getSessionsBy(user.getUserId());
        if (null != sessions && sessions.size() > 0) {
            return false;
        }
        return true;
    }
}
