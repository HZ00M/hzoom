package com.hzoom.game.config;

import com.hzoom.game.concurrent.GameEventExecutorGroup;
import com.hzoom.game.dao.AsyncPlayerDao;
import com.hzoom.game.dao.PlayerDao;
import com.hzoom.message.config.ChannelServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfiguration {

    @Autowired
    private ChannelServerProperties channelServerProperties;//注入配置信息

    private GameEventExecutorGroup dbExecutorGroup;
    @Autowired
    private PlayerDao playerDao; //注入数据库操作类

    @PostConstruct
    public void init() {
        dbExecutorGroup = new GameEventExecutorGroup(channelServerProperties.getDbThreads());//初始化db操作的线程池组
    }

    @Bean
    public AsyncPlayerDao asyncPlayerDao() {//配置AsyncPlayerDao的Bean
        return new AsyncPlayerDao(dbExecutorGroup, playerDao);
    }
}