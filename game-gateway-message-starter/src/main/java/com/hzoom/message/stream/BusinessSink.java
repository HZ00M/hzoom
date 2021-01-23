package com.hzoom.message.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface BusinessSink {
//    String test = "test";
    String business = "business";
    String rpcRequest = "rpc-request";
    String rpcResponse = "rpc-response";
//    @Input(test)
//    SubscribableChannel test();
    @Input(business)
    SubscribableChannel business();
    @Input(rpcRequest)
    SubscribableChannel rpcRequest();
    @Input(rpcResponse)
    SubscribableChannel rpcResponse();
}
