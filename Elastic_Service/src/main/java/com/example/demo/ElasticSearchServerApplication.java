package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {RestClientAutoConfiguration.class})
@EnableDiscoveryClient
public class ElasticSearchServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticSearchServerApplication.class, args);
    }

}
