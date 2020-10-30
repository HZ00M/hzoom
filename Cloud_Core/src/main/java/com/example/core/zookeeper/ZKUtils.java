package com.example.core.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZKUtils {
    @Autowired
    private CuratorFramework client;

    public void destroy() {
        CloseableUtils.closeQuietly(client);
    }

    public CuratorFramework getClient(){
        return this.client;
    }

    /**
     * 创建节点
     */
    public String createNode(String zkPath, String data,CreateMode createMode) {
        try {
            // 创建一个 ZNode 节点
            // 节点的数据为 payload
            byte[] payload = "to set content".getBytes("UTF-8");
            if (data != null) {
                payload = data.getBytes("UTF-8");
            }
            return client.create()
                    .creatingParentsIfNeeded()
                    .withMode(createMode)
                    .forPath(zkPath, payload);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除节点
     */
    public void deleteNode(String zkPath) {
        try {
            if (!isNodeExist(zkPath)) {
                return;
            }
            client.delete()
                    .forPath(zkPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查节点
     */
    public boolean isNodeExist(String zkPath) {
        try {
            Stat stat = client.checkExists().forPath(zkPath);
            if (null == stat) {
                log.info("node not exist", zkPath);
                return false;
            } else {
                log.info("node had been exist, stat is:", stat.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
