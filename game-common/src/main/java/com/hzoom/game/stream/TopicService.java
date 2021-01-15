package com.hzoom.game.stream;

import com.hzoom.game.config.TopicProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
public class TopicService {
    @Autowired(required = false)
    private BinderAwareChannelResolver resolver;
    @Autowired(required = false)
    private TopicProperties topicProperties;

    public void sendMessage(String body, String topic) {
        sendMessage(body.getBytes(), topic);
    }

    public void sendMessage(byte[] body, String topic) {
        resolver.resolveDestination(topic).send(new GenericMessage<>(body));
    }

    public static String generateTopic(String prefix, int serverId) {
        return prefix + "-" + serverId;
    }
}
