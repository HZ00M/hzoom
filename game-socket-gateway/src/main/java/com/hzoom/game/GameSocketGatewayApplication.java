package com.hzoom.game;

import com.hzoom.game.server.GatewayServerBoot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding
public class GameSocketGatewayApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GameSocketGatewayApplication.class, args);
        GatewayServerBoot serverBoot = context.getBean(GatewayServerBoot.class);
        serverBoot.startServer();
    }
}
