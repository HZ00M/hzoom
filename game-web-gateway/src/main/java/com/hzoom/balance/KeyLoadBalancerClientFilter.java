package com.hzoom.balance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

public class KeyLoadBalancerClientFilter extends LoadBalancerClientFilter {
    public KeyLoadBalancerClientFilter(LoadBalancerClient loadBalancer, LoadBalancerProperties properties) {
        super(loadBalancer, properties);
    }

    @Override
    protected ServiceInstance choose(ServerWebExchange exchange) {
        //从header中获取一个openId，做为负载服务实例的key
        String routeKey = exchange.getRequest().getHeaders().getFirst("openId");
        if (routeKey==null){
            return super.choose(exchange);
        }
        if (this.loadBalancer instanceof RibbonLoadBalancerClient){
            RibbonLoadBalancerClient client = (RibbonLoadBalancerClient) this.loadBalancer;
            String serviceId = ((URI) exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR)).getHost();
            return client.choose(serviceId, routeKey);
        }
        return super.choose(exchange);
    }
}
