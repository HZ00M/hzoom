package com.example.core.netty.web.autoconfigure;

import com.example.core.netty.web.core.ServerEndpointRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(ServerEndpointRegistrar.class)
@Configuration
public class NettyWebSocketSelector {

    @Bean
    public ServerEndpointRegistrar serverEndpointRegistrar() {
        return new ServerEndpointRegistrar();
    }
}
