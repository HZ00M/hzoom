package com.hzoom.im.session;

import com.hzoom.core.concurrent.callbackTask.CallbackTask;
import com.hzoom.core.concurrent.callbackTask.CallbackTaskScheduler;
import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.proto.ProtoMsg;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");
    private Channel channel;
    private UserDTO user;
    private String sessionId;
    private boolean isConnected = false;
    private boolean isLogin = false;
    private Map<String, Object> map = new HashMap<String, Object>();

    //双向绑定
    public ClientSession(Channel channel) {
        this.channel = channel;
        this.sessionId = String.valueOf(-1);
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }

    public boolean isConnected() {
        return isConnected;
    }

    //登录成功之后,设置sessionId
    public void loginSuccess(ProtoMsg.Message pkg) {
        sessionId = pkg.getSessionId();
        isLogin = true;
        log.info("登录成功");
    }

    //获取channel
    public static ClientSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
        return session;
    }

    //获取远程地址
    public String getRemoteAddress() {
        return channel.remoteAddress().toString();
    }

    public void sendMsg(ProtoMsg.Message message) {

        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {

                if (!isConnected()) {
                    log.info("连接还没成功");
                    throw new Exception("连接还没成功");
                }
                final Boolean[] isSuccess = {false};
                ChannelFuture f = send(message);
                f.addListener((Future<? super Void> future) -> {
                    //回调
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
            public void onBack(Boolean b) {
                if (b) {
                    sendSuccess(message);
                } else {
                    sendFailed(message);
                }
            }

            @Override
            public void onException(Throwable t) {
                sendException(t);
            }
        });

    }

    //发送数据帧
    public ChannelFuture send(Object pkg) {
        ChannelFuture f = channel.writeAndFlush(pkg);
        return f;
    }

    protected void sendSuccess(ProtoMsg.Message message) {
        log.info("发送成功");
    }

    protected void sendFailed(ProtoMsg.Message message) {
        log.info("发送失败");
    }

    protected void sendException(Throwable throwable) {
        log.info("发送消息出现异常");
    }

    //关闭通道
    public ChannelFuture close() throws InterruptedException {
        isConnected = false;
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.error("连接顺利断开");
                }
            }
        });
        return future;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public boolean isLogin() {
        return this.isLogin;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId(){
        return sessionId;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
