package com.hzoom.im.user.dao;

import com.hzoom.im.user.UserImNodes;

public interface UserSessionsDAO {
    void save(UserImNodes s);

    UserImNodes get(String sessionId);

    void cacheUser(String uid, String sessionId);

    void removeUserSession(String uid, String sessionId);
}
