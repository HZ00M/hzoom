package com.hzoom.game.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "game.gateway.config")
@Configuration
public class GatewayServerProperties {

    /**
     *  服务端口
     */
    private int port;
    /**
     * select 线程数
     */
    private int bossThreadCount;
    /**
     *  work线程数
     */
    private int workThreadCount;
    /**
     * 接收包大小
     */
    private long recBufSize;
    /**
     * 发送包大小
     */
    private long sendBufSize;
    /**
     * channel读取空闲时间
     */
    private int readerIdleTimeSeconds = 300;
    /**
     * channel写出空闲时间
     */
    private int writerIdleTimeSeconds = 12;
    /**
     * 读写空闲时间
     */
    private int allIdleTimeSeconds = 15;
    /**
     * 达到压缩的消息最小大小
     */
    private int compressMessageSize = 1024 * 2;
    /**
     * 等待认证的超时时间
     */
    private int waiteConfirmTimeoutSecond = 600;
    /**
     * 单个用户的限流请允许的每秒请求数量
     */
    private double requestPerSecond = 10;
    /**
     * 全局流量限制请允许每秒请求数量
     */
    private double globalRequestPerSecond=2000;

}
