package com.hzoom.message.enums;

import com.hzoom.message.config.BusinessChannelAutoConfiguration;
import com.hzoom.message.config.GatewayChannelAutoConfiguration;
import lombok.Getter;

public enum ChannelType{
    GATEWAY(GatewayChannelAutoConfiguration.class.getName()),
    BUSINESS(BusinessChannelAutoConfiguration.class.getName()),
    ;
    @Getter
    private String channelType;
    ChannelType(String channelType){
        this.channelType = channelType;
    }
}
