package com.hzoom.im.stream.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
public class TopicService {
    @Autowired
    private BinderAwareChannelResolver resolver;

    public void sendMessage(String body, String topic) {
        sendMessage(body.getBytes(), topic);
    }
    public void sendMessage(byte[] body, String topic) {
        resolver.resolveDestination(topic).send(new GenericMessage<>(body));
    }

}
