package com.hzoom.game.filter;

import com.hzoom.common.error.TokenException;
import com.hzoom.common.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class TokenVerifyFilter implements GlobalFilter, Ordered {
    @Autowired
    private GatewayFilterProperties filterProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUri = exchange.getRequest().getURI().getPath();
        List<String> whiteRequestUri = filterProperties.getWhiteRequestUri();
        if (whiteRequestUri.contains(requestUri)){
            return chain.filter(exchange);// 如果请求的uri在白名单中，则跳过验证。
        }
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)){
            log.info("{} 请求验证失败，token为空",token);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            JWTUtil.TokenBody tokenBody = JWTUtil.getTokenBody(token);
            // 把token中的openId和userId添加到Header中，转发到后面的服务。
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("openId", tokenBody.getOpenId())
                    .header("userId", String.valueOf(tokenBody.getUserId())).build();
            ServerWebExchange newExchange = exchange.mutate().request(request).build();
            return chain.filter(newExchange);
        } catch (TokenException e) {
            log.info("{} 请求验证失败,token非法",token);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
