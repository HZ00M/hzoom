package com.hzoom.im.session;

import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.utils.JsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class LocalSession implements ServerSession {
    public static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("USER_ID_KEY");
    public static final AttributeKey<LocalSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    private Map<String, Object> map = new HashMap<>();

    private Channel channel;
    private final String sessionId;
    private UserDTO user;
    private boolean isLogin = false;

    public LocalSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }

    /**
     * 反向导航
     */
    public static LocalSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(SESSION_KEY).get();
    }

    /**
     * 绑定channel
     */
    public LocalSession bind() {
        channel.attr(SESSION_KEY).set(this);
        channel.attr(ServerConstants.NODE_KEY).set(JsonUtil.pojoToJson(user));
        isLogin = true;
        return this;
    }

    public LocalSession unbind() {
        isLogin = false;
        close();
        return this;
    }

    @Override
    public ChannelFuture send(Object pkg) {
        return channel.writeAndFlush(pkg);
    }

    @Override
    public ChannelFuture close() {
        ChannelFuture future = channel.close();
        future.addListener((ChannelFuture f) -> {
            if (!f.isSuccess()) {
                log.error("close session {} error", sessionId);
            }
        });
        return future;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public boolean isValid() {
        return getSessionUser() != null;
    }

    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public UserDTO getSessionUser() {
        return user;
    }

    public void setSessionUser(UserDTO user) {
        this.user = user;
        user.setSessionId(sessionId);
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public synchronized void set(String key, Object value) {
        map.put(key, value);
    }

    public synchronized <T> T get(String key) {
        return (T) map.get(key);
    }

    public UserDTO getUser() {
        return user;
    }
}
