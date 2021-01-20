package com.hzoom.game.stream;

import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.server.PlayerChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GatewayMessageService {
    @Autowired
    private PlayerChannelManager playerChannelManager;

    @StreamListener(TopicDefine.GATEWAY_TOPIC)
    public void receive(byte[] payload) {
        MessagePackage messagePackage = MessagePackage.readMessagePackage(payload);
        long playerId = messagePackage.getHeader().getPlayerId();
        Channel channel = playerChannelManager.getChannel(playerId);
        if (channel != null) {
            channel.writeAndFlush(messagePackage);
        }
    }
}
