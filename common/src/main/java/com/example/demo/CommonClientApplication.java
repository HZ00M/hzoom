package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class CommonClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonClientApplication.class, args);
    }

}
