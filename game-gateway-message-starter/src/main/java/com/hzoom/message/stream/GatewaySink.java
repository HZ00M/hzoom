package com.hzoom.message.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GatewaySink {
    String gateway = "gateway";
    @Input(gateway)
    SubscribableChannel gatewayTopic();
}
