package com.hzoom.im.user.dao.impl;

import com.hzoom.core.redis.RedisUtils;
import com.hzoom.im.distributed.Peer;
import com.hzoom.im.user.UserImNodes;
import com.hzoom.im.user.dao.UserSessionsDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserSessionsRedisImpl implements UserSessionsDAO {
    @Autowired
    private Peer peer;
    public static final String REDIS_PREFIX = "UserSessions:uid:";
    private static final int EXPIRE_TIME = 60 * 4;//4小时
    @Autowired
    RedisUtils redisUtils;

    @Override
    public void save(UserImNodes userImNodes) {
        String redisKey = getRedisKey(userImNodes.getUserId());
        redisUtils.setexObject(redisKey,EXPIRE_TIME,redisKey);
    }

    @Override
    public UserImNodes get(String userId) {
        String redisKey = getRedisKey(userId);
        return redisUtils.getObject(redisKey, UserImNodes.class);
    }

    @Override
    public void cacheUser(String uid, String sessionId) {
        UserImNodes userSessions = get(sessionId);
        if (null==userSessions){
            userSessions =new UserImNodes(uid);
        }
        userSessions.addNodeBySessionId(sessionId,peer.getLocalImNode());
        save(userSessions);
        log.info("分布式 session增加：uid {} sessionId{}",uid,sessionId);
    }

    @Override
    public void removeUserSession(String userId, String sessionId) {
        UserImNodes userImNodes = get(userId);
        if (null== userImNodes){
            userImNodes = new UserImNodes(userId);
        }
        userImNodes.removeNodeBySessionId(sessionId);
        save(userImNodes);
    }

    public static String getRedisKey(String userId){
        return REDIS_PREFIX + userId;
    }
}
