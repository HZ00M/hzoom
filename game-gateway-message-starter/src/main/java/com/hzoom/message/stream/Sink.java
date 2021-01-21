package com.hzoom.message.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sink {
    String business = "business";
    String rpcRequest = "rpc-request";
    String rpcResponse = "rpc-response";
    @Input(business)
    SubscribableChannel business();
    @Input(rpcRequest)
    SubscribableChannel rpcRequest();
    @Input(rpcResponse)
    SubscribableChannel rpcResponse();
}
