package com.hzoom.im.sender;

import com.hzoom.core.concurrent.callbackTask.CallbackTask;
import com.hzoom.core.concurrent.callbackTask.CallbackTaskScheduler;
import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.session.ClientSession;
import com.hzoom.im.proto.ProtoMsg;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class Sender {
    private UserDTO user;
    private ClientSession session;

    public void sendMsg(ProtoMsg.Message message) {
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                if (session == null) {
                    throw new Exception("session is null");
                }
                if (!isConnected()) {
                    log.info("连接还没成功");
                    throw new Exception("连接还没成功");
                }
                ChannelFuture f = session.send(message);
                final Boolean[] isSuccess = {false};
                f.addListener(future -> {
                    if (future.isSuccess()) {
                        isSuccess[0] = true;
                    }
                });
                try {
                    f.sync();
                } catch (InterruptedException e) {
                    isSuccess[0] = false;
                    e.printStackTrace();
                    throw new Exception("error occur");
                }
                return isSuccess[0];
            }

            @Override
            public void onBack(Boolean success) {
                if (success) {
                    sendSuccess(message);
                } else {
                    sendFailed(message);
                }
            }

            @Override
            public void onException(Throwable t) {
                sendException(message);
            }
        });
    }

    public boolean isConnected() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isConnected();
    }

    public boolean isLogin() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isLogin();
    }

    protected void sendSuccess(ProtoMsg.Message message) {
        log.info("发送成功");
    }

    protected void sendFailed(ProtoMsg.Message message) {
        log.info("发送失败");
    }

    protected void sendException(ProtoMsg.Message message) {
        log.info("发送消息出现异常");
    }


}
