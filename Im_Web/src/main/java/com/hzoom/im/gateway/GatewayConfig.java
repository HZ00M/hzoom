package com.hzoom.im.gateway;

import com.hzoom.im.gateway.factory.RequestTimeGatewayFilterFactory;
import com.hzoom.im.gateway.filter.RequestTimeFilter;
import com.hzoom.im.gateway.global.TokenFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GatewayConfig {
//    @Bean
    public RouteLocator beginTimeRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/foo/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .filter(new RequestTimeFilter()).addResponseHeader(RequestTimeFilter.REQUEST_TIME_BEGIN, "test"))
                                .uri("http://localhost:8888/")
                                .order(0)
                                .id("test_route")
                        )
                .build();
    }

    @Bean
    public RequestTimeGatewayFilterFactory requestTimeGatewayFilterFactory(){
        return new RequestTimeGatewayFilterFactory();
    }

    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter();
    }
}
