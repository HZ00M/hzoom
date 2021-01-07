package com.hzoom.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class GameSocketGatewayApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GameSocketGatewayApplication.class, args);
    }
}
