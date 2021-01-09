package com.hzoom.game.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "topic-share-config")
@Configuration
@Data
public class TopicProperties {
    private String gameTopic;
}
