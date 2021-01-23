package com.hzoom.game;

import com.hzoom.game.server.GatewayServerBoot;
import com.hzoom.message.annotation.StartChannelServer;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.message.enums.ChannelType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@StartChannelServer(ChannelType.GATEWAY)
public class GameSocketGatewayApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GameSocketGatewayApplication.class, args);
        GatewayServerBoot serverBoot = context.getBean(GatewayServerBoot.class);
        serverBoot.startServer();
    }
}
