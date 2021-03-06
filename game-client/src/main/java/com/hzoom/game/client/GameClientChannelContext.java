package com.hzoom.game.client;

import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.message.dispatcher.IChannelContext;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameClientChannelContext implements IChannelContext {
    @Getter
    private Channel channel;
    private IMessage request;

    public GameClientChannelContext(Channel channel, IMessage request) {
        super();
        this.channel = channel;
        this.request = request;
    }

    @Override
    public void sendMessage(IMessage gameMessage) {
        if (channel.isActive() && channel.isOpen()) {
            channel.writeAndFlush(gameMessage);
        } else {
            log.trace("channel {} 已失效，发消息失败", channel.id().asShortText());
        }
    }

    @Override
    public <T> T getRequest() {
        return (T) request;
    }


    @Override
    public long getPlayerId() {
        return 0;
    }
}
