package com.example.demo.activiti.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.apache.commons.lang.builder.ToStringBuilder;

@Slf4j
public class TimeInterceptor extends AbstractCommandInterceptor {
    @Override
    public <T> T execute(CommandConfig commandConfig, Command<T> command) {
        long start = System.currentTimeMillis();
        try {
            return next.execute(commandConfig, command);
        }finally {
            long duration = System.currentTimeMillis() - start;
            log.info("{} exec duration time {}", command.getClass().getSimpleName(),duration);
        }
    }
}
