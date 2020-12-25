package hzoom.filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 全局限流器
 */
@Service
@Slf4j
public class RequestRateLimiterFilter implements GlobalFilter, Ordered {
    @Autowired
    private GatewayFilterProperties filterProperties;
    private RateLimiter globalRateLimiter;
    private LoadingCache<String,RateLimiter> userRateLimiterCache;
    @PostConstruct
    public void init(){
        double permitsPerSecond = filterProperties.getGlobalRequestRateCount();
        globalRateLimiter = RateLimiter.create(permitsPerSecond);
        int maximumSize = filterProperties.getCacheUserMaxCount();
        int duration = filterProperties.getCacheUserTimeout();
        CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess(duration, TimeUnit.MILLISECONDS).build(new CacheLoader<String, RateLimiter>() {
            @Override
            public RateLimiter load(String key) throws Exception {
                // 不存在限流器就创建一个。
                double permitsPerSecond = filterProperties.getUserRequestRateCount();
                RateLimiter newRateLimiter = RateLimiter.create(permitsPerSecond);
                return newRateLimiter;
            }
        });
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String openId = exchange.getRequest().getHeaders().getFirst("openId");
        if (!StringUtils.isEmpty(openId)){
            try {
                RateLimiter userRateLimiter = userRateLimiterCache.get(openId);
                if (!userRateLimiter.tryAcquire()){
                    return this.tooManyRequest(exchange, chain);
                }
            }catch (ExecutionException e){
                log.error("限流器异常", e);
                return this.tooManyRequest(exchange, chain);
            }
        }
        if (!globalRateLimiter.tryAcquire()){
            return this.tooManyRequest(exchange, chain);
        }
        return chain.filter(exchange);// 成功获取令牌，放行
    }

    private Mono<Void> tooManyRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("请求太多，触发限流");
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);// 请求失败，返回请求太多
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
