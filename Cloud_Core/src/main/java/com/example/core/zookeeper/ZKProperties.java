package com.example.core.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zk")
@Data
public class ZKProperties {
    private String connectionString = "localhost:2181";

    private Integer connectionTimeoutMs = 15000;

    private Integer sessionTimeoutMs =  60000;

}
