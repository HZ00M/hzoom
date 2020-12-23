package com.hzoom.core.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.WatcherRemoveCuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.TransactionOp;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.schema.SchemaSet;
import org.apache.curator.framework.state.ConnectionStateErrorPolicy;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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


    /*********************原生方法*********************/
    
    public void start() {
        client.start();
    }

    
    public void close() {
        client.close();
    }

    
    public CuratorFrameworkState getState() {
        return client.getState();
    }

    
    public boolean isStarted() {
        return client.isStarted();
    }

    
    public CreateBuilder create() {
        return client.create();
    }

    
    public DeleteBuilder delete() {
        return client.delete();
    }

    
    public ExistsBuilder checkExists() {
        return client.checkExists();
    }

    
    public GetDataBuilder getData() {
        return client.getData();
    }

    
    public SetDataBuilder setData() {
        return client.setData();
    }

    
    public GetChildrenBuilder getChildren() {
        return client.getChildren();
    }

    
    public GetACLBuilder getACL() {
        return client.getACL();
    }

    
    public SetACLBuilder setACL() {
        return client.setACL();
    }

    
    public ReconfigBuilder reconfig() {
        return client.reconfig();
    }

    
    public GetConfigBuilder getConfig() {
        return client.getConfig();
    }

    
    public CuratorTransaction inTransaction() {
        return client.inTransaction();
    }

    
    public CuratorMultiTransaction transaction() {
        return client.transaction();
    }

    
    public TransactionOp transactionOp() {
        return client.transactionOp();
    }

    
    public void sync(String s, Object o) {
        client.sync(s, o);
    }

    
    public void createContainers(String s) throws Exception {
        client.createContainers(s);
    }

    
    public SyncBuilder sync() {
        return client.sync();
    }

    
    public RemoveWatchesBuilder watches() {
        return client.watches();
    }

    
    public Listenable<ConnectionStateListener> getConnectionStateListenable() {
        return client.getConnectionStateListenable();
    }

    
    public Listenable<CuratorListener> getCuratorListenable() {
        return client.getCuratorListenable();
    }

    
    public Listenable<UnhandledErrorListener> getUnhandledErrorListenable() {
        return client.getUnhandledErrorListenable();
    }

    
    public CuratorFramework nonNamespaceView() {
        return client.nonNamespaceView();
    }

    
    public CuratorFramework usingNamespace(String s) {
        return client.usingNamespace(s);
    }

    
    public String getNamespace() {
        return client.getNamespace();
    }

    
    public CuratorZookeeperClient getZookeeperClient() {
        return client.getZookeeperClient();
    }

    
    public EnsurePath newNamespaceAwareEnsurePath(String s) {
        return client.newNamespaceAwareEnsurePath(s);
    }

    
    public void clearWatcherReferences(Watcher watcher) {
        client.clearWatcherReferences(watcher);
    }

    
    public boolean blockUntilConnected(int i, TimeUnit timeUnit) throws InterruptedException {
        return client.blockUntilConnected(i,timeUnit);
    }

    
    public void blockUntilConnected() throws InterruptedException {
        client.blockUntilConnected();
    }

    
    public WatcherRemoveCuratorFramework newWatcherRemoveCuratorFramework() {
        return client.newWatcherRemoveCuratorFramework();
    }

    
    public ConnectionStateErrorPolicy getConnectionStateErrorPolicy() {
        return client.getConnectionStateErrorPolicy();
    }

    
    public QuorumVerifier getCurrentConfig() {
        return client.getCurrentConfig();
    }

    
    public SchemaSet getSchemaSet() {
        return client.getSchemaSet();
    }

    
    public boolean isZk34CompatibilityMode() {
        return client.isZk34CompatibilityMode();
    }
}
