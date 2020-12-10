package com.hzoom.im.session;

import com.hzoom.im.bean.Notification;
import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.distributed.OnlineCounter;
import com.hzoom.im.distributed.Peer;
import com.hzoom.im.distributed.Router;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.user.UserSessions;
import com.hzoom.im.user.dao.UserSessionsDAO;
import com.hzoom.im.utils.JsonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Component
public class SessionManger {
    @Autowired
    private OnlineCounter onlineCounter;
    @Autowired
    private UserSessionsDAO userSessionsDAO;
    @Autowired
    private Peer peer;
    @Autowired
    private Router router;
    /*本地会话集合*/
    private ConcurrentHashMap<String, LocalSession> localSessionMap = new ConcurrentHashMap();
    /*远程会话集合*/
    private ConcurrentHashMap<String, RemoteSession> remoteSessionMap = new ConcurrentHashMap();
    /*本地用户集合*/
    private ConcurrentHashMap<String, UserSessions> localUserSessionsMap = new ConcurrentHashMap();

    /**
     * 添加本地回话
     */
    public void addLocalSession(LocalSession localSession) {
        String sessionId = localSession.getSessionId();
        localSessionMap.put(sessionId, localSession);
        String userId = localSession.getSessionUser().getUserId();
        onlineCounter.increment();
        log.info("本地session增加：{},  在线总数:{} ",
                JsonUtil.pojoToJson(localSession.getSessionUser()),
                onlineCounter.getCurValue());
        userSessionsDAO.cacheUser(userId, sessionId);

        /**通知其他节点**/
        notifyOtherImNode(localSession, Notification.SESSION_ON);
    }

    /**
     * 删除本地回话
     */
    public void removeLocalSession(String sessionId) {
        if (!localSessionMap.containsKey(sessionId)) {
            return;
        }
        LocalSession localSession = localSessionMap.get(sessionId);
        UserDTO sessionUser = localSession.getSessionUser();
        localSessionMap.remove(sessionId);

        //在线用户减1
        onlineCounter.decrement();
        log.info("本地session减少：{},  在线总数:{} ",
                JsonUtil.pojoToJson(localSession.getUser()),
                onlineCounter.getCurValue());
        peer.decrBalance();

        //分布式保存user和所有session
        userSessionsDAO.removeUserSession(sessionUser.getUserId(), sessionId);

        /**通知其他节点**/
        notifyOtherImNode(localSession, Notification.SESSION_OFF);
    }

    /**
     * 通知其他节点
     */
    private void notifyOtherImNode(LocalSession session, int type) {
        log.info("通知其他节点加入节点信息 session {} ",session);
        UserDTO user = session.getUser();
        RemoteSession remoteSession = RemoteSession.builder()
                .sessionId(session.getSessionId())
                .imNode(peer.getLocalImNode())
                .userId(user.getUserId())
                .valid(true)
                .build();
        Notification<RemoteSession> notification = new Notification<>(remoteSession);
        notification.setType(type);
        router.sendNotification(JsonUtil.pojoToJson(notification));
    }

    /**
     * 根据用户id，获取session对象
     */
    public List<ServerSession> getSessionsBy(String userId) {
        List<ServerSession> sessions = new LinkedList<>();
        UserSessions userSessions = loadFromCache(userId);
        if (null == userSessions) {
            return Collections.emptyList();
        }
        Map<String, ImNode> allSession = userSessions.getImNodeCache();
        allSession.keySet().stream().forEach(sessionId -> {
            //首先取得本地的session
            ServerSession serverSession = localSessionMap.get(sessionId);
            //没有命中，取得远程的session
            if (null == serverSession) {
                serverSession = remoteSessionMap.get(sessionId);
            }
            sessions.add(serverSession);
        });
        return sessions;
    }

    /**
     * 从一级缓存加载
     *
     * @param userId 用户的id
     * @return 用户的集合
     */
    private UserSessions loadFromCache(String userId) {
        //从本地缓存获取
        UserSessions userSessions = localUserSessionsMap.get(userId);
        if (null != userSessions && null != userSessions.getImNodeCache() && userSessions.getImNodeCache().size() > 0) {
            return userSessions;
        }
        //从本地回话获取
        UserSessions finalUserSessions = new UserSessions(userId);
        ;
        localSessionMap.values().stream().forEach(session -> {
            if (userId.equals(session.getSessionUser().getUserId())) {
                finalUserSessions.addLocalNode(session);
            }
        });
        localUserSessionsMap.put(userId, finalUserSessions);
        return finalUserSessions;
    }

    /**
     * 从二级缓存加载
     *
     * @param userId 用户的id
     * @return 用户的集合
     */
    private UserSessions loadFromRedis(String userId) {
        //从redis缓存获取
        UserSessions userSessions = userSessionsDAO.get(userId);
        if (null == userSessions) {
            return null;
        }
        Map<String, ImNode> map = userSessions.getImNodeCache();
        map.keySet().stream().forEach(key -> {
            ImNode node = map.get(key);
            //当前节点直接忽略
            if (!node.equals(peer.getLocalImNode())) {
                remoteSessionMap.put(key, new RemoteSession(key, userId, node));
            }
        });

        return userSessions;
    }

    /**
     * 增加 远程的 session
     */
    public void addRemoteSession(RemoteSession remoteSession) {
        String sessionId = remoteSession.getSessionId();
        if (localSessionMap.containsKey(sessionId)) {
            log.error("通知有误，通知到了会话所在的节点");
            return;
        }
        remoteSessionMap.put(sessionId, remoteSession);

        //添加本地保存的 远程session
        String userId = remoteSession.getUserId();
        UserSessions userSessions = localUserSessionsMap.get(userId);
        if (null == userId) {
            userSessions = new UserSessions(userId);
            localUserSessionsMap.put(userId, userSessions);
        }
        userSessions.addNodeBySessionId(sessionId, remoteSession.getImNode());
    }

    /**
     * 删除 远程的 session
     */
    public void removeRemoteSession(String sessionId) {
        if (localSessionMap.containsKey(sessionId)) {
            log.error("通知有误，通知到了会话所在的节点");
            return;
        }

        RemoteSession remoteSession = remoteSessionMap.get(sessionId);
        remoteSessionMap.remove(sessionId);

        //删除本地保存的 远程session
        String userId = remoteSession.getUserId();
        UserSessions userSessions = localUserSessionsMap.get(userId);
        userSessions.removeNodeBySessionId(sessionId);
    }

    /**
     * 关闭连接
     */
    public ChannelFuture closeSession(ChannelHandlerContext ctx) {
        LocalSession localSession = ctx.channel().attr(LocalSession.SESSION_KEY).get();
        ChannelFuture future = null;
        if (null != localSession && localSession.isValid()) {
            future = localSession.close();
            removeLocalSession(localSession.getSessionId());
        }
        return future;
    }

}
