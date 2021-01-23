package com.hzoom.game.config;

import com.hzoom.message.config.ChannelServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Autowired
    private ChannelServerProperties channelServerProperties;

}
