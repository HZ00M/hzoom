package com.example.demo.sharding;

import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.util.Properties;
import java.util.UUID;

/**
 * 由于扩展ShardingKeyGenerator是通过JDK的serviceloader的SPI机制实现的，
 * 因此还需要在resources/META-INF/services目录下配置
 * org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator文件。
 *
 * SPI的英文名称是Service Provider Interface，是Java 内置的服务发现机制。
 * 在开发过程中，将问题进抽象成API，可以为API提供各种实现。
 * 如果现在需要对API提供一种新的实现，我们可以不用修改原来的代码，直接生成新的Jar包，
 * 在包里提供API的新实现。通过Java的SPI机制，可以实现了框架的动态扩展，让第三方的实现能像插件一样嵌入到系统中。
 */
public class SeqShardingKeyGenerator implements ShardingKeyGenerator {
    Properties properties = new Properties();

    @Override
    public Comparable<?> generateKey() {
        // 获取分布式id逻辑
        return System.currentTimeMillis();
    }

    @Override
    public String getType() {
        return "SEQ";
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
