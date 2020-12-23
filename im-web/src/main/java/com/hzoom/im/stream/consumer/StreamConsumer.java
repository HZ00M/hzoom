package com.hzoom.im.stream.consumer;

import com.hzoom.im.stream.define.StreamClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(value = {StreamClient.class})
@Slf4j
public class StreamConsumer {
    @StreamListener(StreamClient.INPUT)
    public void receive(String message){
        log.info("StreamConsumer receive : "+message);
    }
}
