package com.hzoom.core.redis;

import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.SSLSocketFactory;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({RedisProperties.class})
@ConditionalOnClass
@Import({RedisDistributedAspectRegistrar.class, RedisService.class})
@ConditionalOnProperty(
        prefix = "spring.redis",
        name = {"enable"},
        havingValue = "true",
        matchIfMissing = false
)
public class RedissonClientAutoConfigure {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    @ConditionalOnMissingBean
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public JedisPool getJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
        //MaxWaitMillis默认-1，表示无限等待，最好不使用默认值
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
        jedisPoolConfig.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
        jedisPoolConfig.setMinIdle(redisProperties.getJedis().getPool().getMinIdle());

        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            return new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(), redisProperties.getTimeout().getNano(), redisProperties.getPassword());
        }
        SSLSocketFactory.getDefault();
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(), redisProperties.getTimeout().getNano());
        hotPool(jedisPool);
        return jedisPool;
    }

    //jedis连接池预热
    public  void hotPool(JedisPool jedisPool){
        int minIdle = redisProperties.getJedis().getPool().getMinIdle();
        Jedis jedis =null;
        List<Jedis> minIdleJedisList = new ArrayList<>(minIdle);
        for (int i = 0;i<minIdle;i++) {
            try {
                jedis = jedisPool.getResource();
                jedis.ping();
                minIdleJedisList.add(jedis);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for (int i = 0;i<minIdle;i++) {
            try {
                jedis = minIdleJedisList.get(i);
                jedis.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
