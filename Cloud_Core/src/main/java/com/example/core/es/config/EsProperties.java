package com.example.core.es.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "es")
@Data
public class EsProperties {
    private String host = "localhost";
    private Integer port = 9200;
    private String scheme = "http";
    private Index index = new Index(1,1);


}
@Data
@AllArgsConstructor
class Index{
    private int numberOfShards;
    private int numberOfReplicas;
}
