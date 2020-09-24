package com.example.core.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({EsProperties.class})
@ConditionalOnClass
@Import(BaseElasticService.class)
@ConditionalOnProperty(
        prefix = "es",
        name = {"autoConfigurable"},
        havingValue = "true",
        matchIfMissing = false
)
public class EsAutoConfigure {
    @Autowired
    EsProperties esProperties;

    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder());
        return client;
    }

    public RestClientBuilder restClientBuilder(){
        return RestClient.builder(new HttpHost(esProperties.getHost(),esProperties.getPort(),esProperties.getScheme()));
    }

}
