package com.hzoom.im.distributed;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.properties.ConstantsProperties;
import com.hzoom.im.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class Peer {
    @Autowired
    private CuratorFramework client;
    @Autowired
    private ConstantsProperties constantsProperties;

    private ImNode localImNode;

    private String nodePath;



    public void init(InetSocketAddress address) {
        localImNode = new ImNode(address);
        try {
            byte[] payload = JsonUtil.object2JsonBytes(localImNode);

            nodePath = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(constantsProperties.getChildPathPrefix(), payload);

            //为node 设置id
            localImNode.setId(getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return getIdByPath(nodePath);
    }

    public long getIdByPath(String path) {
        String sid = null;
        if (null == path) {
            throw new RuntimeException("节点路径有误");
        }
        int index = path.lastIndexOf(constantsProperties.getChildPathPrefix());
        if (index >= 0) {
            index += constantsProperties.getChildPathPrefix().length();
            sid = index <= path.length() ? path.substring(index) : null;
        }
        if (null == sid) {
            throw new RuntimeException("节点ID获取失败");
        }
        return Long.parseLong(sid);
    }

    private void createParentIfNeeded(String managePath) {
        try {
            Stat stat = client.checkExists().forPath(managePath);
            if (null == stat) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImNode getLocalImNode() {
        return localImNode;
    }

    /**
     * 增加负载，并写回zookeeper
     */
    public boolean incBalance() {
        Assert.isTrue(null != localImNode, "没有设置本地节点");
        while (true) {
            try {
                localImNode.incrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localImNode);
                client.setData().forPath(nodePath, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 减少负载，表示有用户下线，写回zookeeper
     */
    public boolean decrBalance() {
        Assert.isTrue(null != localImNode, "没有设置本地节点");
        while (true) {
            try {
                localImNode.decrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localImNode);
                client.setData().forPath(nodePath, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
