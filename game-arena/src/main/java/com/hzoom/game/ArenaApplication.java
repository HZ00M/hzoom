package com.hzoom.game;

import com.hzoom.game.channel.ArenaGatewayHandler;
import com.hzoom.game.message.DispatchMessageManager;
import com.hzoom.message.annotation.StartChannelServer;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.message.enums.ChannelType;
import com.hzoom.message.handler.GameChannelIdleStateHandler;
import com.hzoom.message.service.BusinessMessageManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.hzoom"}) // 负责连接数据库
@StartChannelServer(ChannelType.BUSINESS)
public class ArenaApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ArenaApplication.class, args);//初始化spring boot环境
        ChannelServerProperties channelServerProperties = context.getBean(ChannelServerProperties.class);//获取配置的实例
        DispatchMessageManager.scanGameMessages(context, channelServerProperties.getServiceId(), "com.hzoom");// 扫描此服务可以处理的消息
        BusinessMessageManager businessMessageManager = context.getBean(BusinessMessageManager.class);//获取网关消息监听实例
        businessMessageManager.start((gameChannel) -> {//启动网关消息监听，并初始化GameChannelHandler
            // 初始化channel
            gameChannel.getChannelPipeline().addLast(new GameChannelIdleStateHandler(120, 120, 100));
            gameChannel.getChannelPipeline().addLast(new ArenaGatewayHandler(context));
        }, channelServerProperties.getServerId());
    }
}
