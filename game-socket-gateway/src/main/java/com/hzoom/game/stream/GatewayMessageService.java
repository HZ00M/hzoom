package com.hzoom.game.stream;

import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.server.PlayerChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@EnableBinding(value = {TopicDefine.class})
@Slf4j
public class GatewayMessageService {
    @Autowired
    private PlayerChannelManager playerChannelManager;

    @StreamListener(TopicDefine.gameLogicTopic)
    public void receive(MessagePackage messagePackage) {
        log.info("接收到业务服务器发送的消息: {}", messagePackage.toString());
        long playerId = messagePackage.getHeader().getPlayerId();
        Channel channel = playerChannelManager.getChannel(playerId);
        if (channel != null) {
            channel.writeAndFlush(messagePackage);
        }
    }
}
