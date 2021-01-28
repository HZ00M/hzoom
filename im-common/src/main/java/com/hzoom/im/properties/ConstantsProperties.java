package com.hzoom.im.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "share-config")
@Configuration
@Data
public class ConstantsProperties {

    private String nodesPath;

    private String childPathPrefix;

    private String counterPath;
}
