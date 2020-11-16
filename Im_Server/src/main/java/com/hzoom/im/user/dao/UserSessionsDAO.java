package com.hzoom.im.user.dao;

import com.hzoom.im.user.UserSessions;

public interface UserSessionsDAO {
    void save(UserSessions s);

    UserSessions get(String sessionId);

    void cacheUser(String uid, String sessionId);

    void removeUserSession(String uid, String sessionId);
}
