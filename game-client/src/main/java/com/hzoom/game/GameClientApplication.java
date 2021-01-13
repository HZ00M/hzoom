package com.hzoom.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding
public class GameClientApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GameClientApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);// 客户端不需要是一个web服务
        app.run(args);// 需要注意的是，由于客户端使用了Spring Shell，它会阻塞此方法，程序不会再往下执行了。
    }
}
