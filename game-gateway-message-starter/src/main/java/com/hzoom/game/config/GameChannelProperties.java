package com.hzoom.game.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ConfigurationProperties(prefix="game.channel")
@Data
public class GameChannelProperties {

    private String businessGameMessageTopic = "business-game-message-topic";//业务服务监听消息的topic
    private String gatewayGameMessageTopic = "gateway-game-message-topic";//网关接收消息监听的topic
    private String rpcRequestGameMessageTopic = "rpc-request-game-messge-topic";
    private String rpcResponseGameMessageTopic = "rpc-response-game-messge-topic";
    private String topicGroupId = "defaultGroupId:" + UUID.randomUUID(); //kafka消息的groupId，一个服务一个唯一的groupId.
    private int workerThreads = 16;

}
