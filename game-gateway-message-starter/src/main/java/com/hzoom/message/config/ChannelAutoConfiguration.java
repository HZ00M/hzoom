package com.hzoom.message.config;

import com.hzoom.message.context.DispatchUserEventManager;
import com.hzoom.message.service.MessageManager;
import com.hzoom.message.stream.Sink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@EnableConfigurationProperties({ChannelServerProperties.class})
@Configuration
@Slf4j
@EnableBinding(Sink.class)
public class ChannelAutoConfiguration implements BeanPostProcessor {

    private ChannelServerProperties channelServerProperties;

    @Bean
    public MessageManager messageManager(){
        return new MessageManager();
    }

    @Bean
    public DispatchUserEventManager dispatchUserEventManager(){
        return new DispatchUserEventManager();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ChannelServerProperties){
            channelServerProperties = (ChannelServerProperties)bean;
        }
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
