package com.hzoom.game.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="game.server.config")
@Data
public class GameServerProperties {
    /**
     * 游戏服务id
     */
    private int serviceId;
    /**
     * 游戏服务所在的服务器id
     */
    private int serverId;
    /**
     * 业务服务监听消息的topic
     */
    private String businessGameMessageTopic = "business-game-message-topic";//业务服务监听消息的topic
    /**
     * 网关接收消息监听的topic
     */
    private String gatewayGameMessageTopic = "gateway-game-message-topic";//网关接收消息监听的topic
    /**
     * 业务处理线程数
     */
    private int workerThreads = 4;//业务处理线程数
    /**
     * db处理线程数
     */
    private int dbThreads = 16;//db处理线程数
    /**
     * 更新redis间隔
     */
    private int flushRedisDelaySecond = 60;
    /**
     * 更新db间隔
     */
    private int flushDBDelaySecond = 300;
}