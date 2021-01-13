package com.hzoom.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding
public class CenterServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CenterServerApplication.class,args);
    }
}
