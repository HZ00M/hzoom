package com.hzoom.im.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "share-config")
@Component
@Data
public class ConstantsProperties {

    private String nodesPath;

    private String childPathPrefix;

    private String counterPath;
}
