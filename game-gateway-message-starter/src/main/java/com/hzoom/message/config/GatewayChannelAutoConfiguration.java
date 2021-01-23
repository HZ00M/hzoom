package com.hzoom.message.config;

import com.hzoom.core.stream.TopicService;
import com.hzoom.message.service.GatewayMessageManager;
import com.hzoom.message.stream.GatewaySink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration
@Slf4j
@EnableBinding(GatewaySink.class)
public class GatewayChannelAutoConfiguration implements BeanPostProcessor{

    private ChannelServerProperties channelServerProperties;

    @Bean
    public GatewayMessageManager messageManager(){
        return new GatewayMessageManager();
    }

    @Bean
    public TopicService topicService(){
        return new TopicService();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ChannelServerProperties){
            channelServerProperties = (ChannelServerProperties)bean;
        }
        if (bean instanceof TopicService){
            log.info("TopicService");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BindingServiceProperties){
            BindingServiceProperties bindingServiceProperties = (BindingServiceProperties)bean;
            Map<String, BindingProperties> binders = bindingServiceProperties.getBindings();
            String gateway = channelServerProperties.getGatewayGameMessageTopic() + "-" + channelServerProperties.getServerId();
            BindingProperties gatewayBinderProperties = new BindingProperties();
            gatewayBinderProperties.setDestination(gateway);
            gatewayBinderProperties.setGroup(channelServerProperties.getTopicGroupId());
            binders.put("gateway", gatewayBinderProperties);
        }
        return bean;
    }
}
