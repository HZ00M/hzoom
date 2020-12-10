package com.hzoom.im.session;

import com.hzoom.im.distributed.PeerSender;
import com.hzoom.im.distributed.Router;
import com.hzoom.im.distributed.SpringManager;
import com.hzoom.im.entity.ImNode;
import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class RemoteSession implements ServerSession, Serializable {
    private static final long serialVersionUID = -400010884211394846L;
    private String userId;
    private String sessionId;
    private ImNode imNode;
    private boolean valid = true;

    public RemoteSession() {
        userId = "";
        sessionId = "";
        imNode = new ImNode("unKnown", 0);
    }

    public RemoteSession(
            String sessionId, String userId, ImNode imNode) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.imNode = imNode;
    }

    @Override
    public ChannelFuture send(Object pkg) {
        long nodeId = imNode.getId();
        Router router = SpringManager.getBean(Router.class);
        PeerSender peerSender = router.getPeerSender(nodeId);
        ChannelFuture future = peerSender.writeAndFlush(pkg);
        return future;
    }

    @Override
    public ChannelFuture close() {
        return null;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
