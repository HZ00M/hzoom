package com.hzoom.game.bean;

import com.hzoom.game.concurrent.GameEventExecutorGroup;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.game.dao.ArenaDao;
import com.hzoom.game.dao.AsyncArenaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfiguration {
    @Autowired
    private ChannelServerProperties channelServerProperties;

    private GameEventExecutorGroup dbExecutorGroup;

    @Autowired
    private ArenaDao arenaDao;

    @PostConstruct
    public void init(){
        dbExecutorGroup = new GameEventExecutorGroup(channelServerProperties.getDbThreads());
    }

    @Bean
    public AsyncArenaDao asyncArenaDao(){
        return new AsyncArenaDao(arenaDao,dbExecutorGroup);
    }
}
