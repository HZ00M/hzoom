package com.hzoom.game;

import com.hzoom.game.config.GameServerProperties;
import com.hzoom.game.context.DispatchUserEventManager;
import com.hzoom.game.dao.AsyncPlayerDao;
import com.hzoom.game.handler.GameChannelIdleStateHandler;
import com.hzoom.game.message.DispatchMessageManager;
import com.hzoom.game.service.MessageConsumerService;
import com.hzoom.game.stream.Sink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.hzoom"}) // 负责连接数据库
@EnableBinding(Sink.class)
public class BirdApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BirdApplication.class, args);//初始化spring boot环境
        GameServerProperties gameServerProperties = context.getBean(GameServerProperties.class);//获取配置的实例
        DispatchMessageManager.scanGameMessages(context, gameServerProperties.getServiceId(), "com.hzoom");// 扫描此服务可以处理的消息
        MessageConsumerService messageConsumerService = context.getBean(MessageConsumerService.class);//获取网关消息监听实例
        AsyncPlayerDao playerDao = context.getBean(AsyncPlayerDao.class);
        DispatchMessageManager dispatchMessageManager= context.getBean(DispatchMessageManager.class);
        DispatchUserEventManager dispatchUserEventManager = context.getBean(DispatchUserEventManager.class);

        messageConsumerService.start((gameChannel) -> {//启动网关消息监听，并初始化GameChannelHandler
            // 初始化channel
            gameChannel.getChannelPipeline().addLast(new GameChannelIdleStateHandler(300, 300, 300));
            gameChannel.getChannelPipeline().addLast(new GameBusinessMessageDispatchHandler(context,gameServerProperties,dispatchMessageManager,dispatchUserEventManager, playerDao));
        },gameServerProperties.getServerId());
    }
}
