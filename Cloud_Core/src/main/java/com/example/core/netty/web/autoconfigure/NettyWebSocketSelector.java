package com.example.core.netty.web.autoconfigure;

import com.example.core.netty.web.endpoint.EndpointRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EndpointRegistrar.class)
@Configuration
public class NettyWebSocketSelector {

    @Bean
    public EndpointRegistrar serverEndpointRegistrar() {
        return new EndpointRegistrar();
    }
}
