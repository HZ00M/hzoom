package com.hzoom.message.config;

import com.hzoom.core.stream.TopicService;
import com.hzoom.message.context.DispatchUserEventManager;
import com.hzoom.message.rpc.DispatchRPCEventManager;
import com.hzoom.message.service.BusinessMessageManager;
import com.hzoom.message.stream.BusinessSink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
@EnableBinding(BusinessSink.class)
@EnableConfigurationProperties({ChannelServerProperties.class})
public class BusinessChannelAutoConfiguration implements BeanPostProcessor{

    @Autowired
    private ChannelServerProperties channelServerProperties;

    @Bean
    public BusinessMessageManager messageManager(){
        return new BusinessMessageManager();
    }

    @Bean
    public DispatchUserEventManager dispatchUserEventManager(){
        return new DispatchUserEventManager();
    }

    @Bean
    public DispatchRPCEventManager dispatchRPCEventManager(){
        return new DispatchRPCEventManager();
    }

    @Bean
    public TopicService topicService(){
        return new TopicService();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BindingServiceProperties){
            BindingServiceProperties bindingServiceProperties = (BindingServiceProperties)bean;
            Map<String, BindingProperties> binders = bindingServiceProperties.getBindings();
            String business = channelServerProperties.getBusinessGameMessageTopic() + "-" + channelServerProperties.getServerId();
            BindingProperties businessBinderProperties = new BindingProperties();
            businessBinderProperties.setDestination(business);
            businessBinderProperties.setGroup(channelServerProperties.getTopicGroupId());
            String rpcRequest = channelServerProperties.getRpcRequestGameMessageTopic() + "-" + channelServerProperties.getServerId();
            BindingProperties requestBinderProperties = new BindingProperties();
            requestBinderProperties.setDestination(rpcRequest);
            requestBinderProperties.setGroup(channelServerProperties.getTopicGroupId());
            String rpcResponse = channelServerProperties.getRpcResponseGameMessageTopic() + "-" + channelServerProperties.getServerId();
            BindingProperties responseBinderProperties = new BindingProperties();
            responseBinderProperties.setDestination(rpcResponse);
            responseBinderProperties.setGroup(channelServerProperties.getTopicGroupId());
            binders.put("business",businessBinderProperties);
            binders.put("rpc-request",requestBinderProperties);
            binders.put("rpc-response",responseBinderProperties);
        }
        return bean;
    }
}
