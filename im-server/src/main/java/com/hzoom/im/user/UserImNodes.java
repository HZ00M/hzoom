package com.hzoom.im.user;

import com.hzoom.im.distributed.Peer;
import com.hzoom.im.distributed.SpringManager;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.session.LocalSession;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class UserImNodes {
    private String userId;
    private Map<String, ImNode> imNodeCache = new LinkedHashMap<>(10);

    public UserImNodes(String userId) {
        this.userId = userId;
    }

    public void addNodeBySessionId(String sessionId, ImNode node) {
        imNodeCache.put(sessionId, node);
    }

    public void removeNodeBySessionId(String sessionId) {
        imNodeCache.remove(sessionId);
    }

    public void addLocalNode(LocalSession session) {
        Peer peer = SpringManager.getBean(Peer.class);
        imNodeCache.put(session.getSessionId(), peer.getLocalImNode());
    }
}
