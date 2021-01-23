package com.hzoom.message.annotation;

import com.hzoom.message.config.BusinessChannelAutoConfiguration;
import com.hzoom.message.config.ChannelImportSelector;
import com.hzoom.message.config.ChannelServerProperties;
import com.hzoom.message.enums.ChannelType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BinderFactoryConfiguration;
import org.springframework.cloud.stream.config.BindingBeansRegistrar;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@EnableConfigurationProperties({ChannelServerProperties.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableBinding
@Import({ChannelImportSelector.class})
public @interface StartChannelServer {
    ChannelType value() ;
}


