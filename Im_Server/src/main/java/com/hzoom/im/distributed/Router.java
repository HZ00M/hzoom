package com.hzoom.im.distributed;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.proto.ProtoMsg;
import com.hzoom.im.protoBuilder.MsgBuilder;
import com.hzoom.im.utils.JsonUtil;
import com.hzoom.im.utils.ObjectUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class Router {
    @Autowired
    private CuratorFramework client;
    @Autowired
    private Peer peer;

    private ConcurrentHashMap<Long, PeerSender> peerSenderMap =
            new ConcurrentHashMap<>();


    public void init() {
        try {
            //订阅节点的增加和删除事件
            PathChildrenCache childrenCache = new PathChildrenCache(client, ServerConstants.MANAGE_PATH, true);
            PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, event) -> {
                log.info("开始监听节点事件:-----");
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("CHILD_ADDED : {} ", data.getPath());
                        processNodeAdded(data);
                        break;
                    case CHILD_REMOVED:
                        log.info("CHILD_REMOVED : {} ", data.getPath());
                        processNodeRemoved(data);
                        break;
                    default:
                        log.info("OTHER_CHILD_EVENT {}", event.getType());
                }
            };
            childrenCache.getListenable().addListener(pathChildrenCacheListener);
            /**
             * （1）NORMAL——异步初始化cache
             *
             * （2）BUILD_INITIAL_CACHE——同步初始化cache
             *
             * （3）POST_INITIALIZED_EVENT——异步初始化cache，并触发完成事件
             */
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 节点增加处理
     *
     * @param data 新节点
     */
    private void processNodeAdded(ChildData data) {
        byte[] payload = data.getData();
        ImNode imNode = ObjectUtil.JsonBytes2Object(payload, ImNode.class);
        long id = peer.getIdByPath(data.getPath());
        imNode.setId(id);
        if (imNode.equals(peer.getLocalImNode())) {
            log.info("[TreeCache]添加本地节点, path={}, data={}",
                    data.getPath(), JsonUtil.pojoToJson(imNode));
            return;
        }else {
            log.info("[TreeCache]添加远程节点, path={}, data={}",
                    data.getPath(), JsonUtil.pojoToJson(imNode));
        }
        /**
         * 重复收到注册的事件
         */
        PeerSender relaySender = peerSenderMap.get(id);
        if (null != relaySender && relaySender.getImNode().equals(imNode)) {
            log.info("[TreeCache]远程节点重复增加, path={}, data={}",
                    data.getPath(), JsonUtil.pojoToJson(imNode));
            return;
        }
        if (null != relaySender) {
            //关闭老的连接
            relaySender.stopConnecting();
        }
        //创建一个消息转发器
        relaySender = new PeerSender(imNode);
        //建立转发的连接
        relaySender.doConnect();
        peerSenderMap.put(id, relaySender);
    }

    /**
     * 节点删除处理
     *
     * @param data 节点信息
     */
    private void processNodeRemoved(ChildData data) {
        byte[] payload = data.getData();
        ImNode imNode = ObjectUtil.JsonBytes2Object(payload, ImNode.class);
        long id = peer.getIdByPath(data.getPath());
        imNode.setId(id);
        log.info("[TreeCache]节点删除, path={}, data={}",
                data.getPath(), JsonUtil.pojoToJson(imNode));
        PeerSender peerSender = peerSenderMap.get(id);
        if (null != peerSender) {
            peerSender.stopConnecting();
            peerSenderMap.remove(id);
        }
    }

    public PeerSender getPeerSender(long id) {
        PeerSender peerSender = peerSenderMap.get(id);
        if (null != peerSender) {
            return peerSender;
        }
        return null;
    }

    public void sendNotification(String json) {
        ProtoMsg.Message pkg = MsgBuilder.buildNotification(json);
        peerSenderMap.keySet().stream().forEach(key -> {
            if (!key.equals(peer.getLocalImNode().getId())) {
                PeerSender peerSender = peerSenderMap.get(key);
                peerSender.writeAndFlush(pkg);
            }
        });
    }
}
