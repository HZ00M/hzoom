package com.hzoom.message.annotation;

import com.hzoom.message.config.ChannelImportSelector;
import com.hzoom.message.enums.ChannelType;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableBinding
@Import({ChannelImportSelector.class})
public @interface StartChannelServer {
    ChannelType value() ;
}


