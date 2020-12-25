package hzoom.balance;

import com.netflix.loadbalancer.IRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LoadBalanceConfiguration {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public KeyLoadBalancerClientFilter keyLoadBalancerClientFilter(LoadBalancerClient client, LoadBalancerProperties properties){
        return new KeyLoadBalancerClientFilter(client,properties);
    }

    @Bean
    public IRule balanceRule(){
        return new KeyLoadBalancerRule();
    }
}
