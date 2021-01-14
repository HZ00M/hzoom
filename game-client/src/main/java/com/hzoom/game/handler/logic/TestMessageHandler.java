package com.hzoom.game.handler.logic;

import com.hzoom.game.client.GameClientChannelContext;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.response.FirstMsgResponse;
import com.hzoom.game.message.response.SecondMsgResponse;
import lombok.extern.slf4j.Slf4j;

@MessageHandler
@Slf4j
public class TestMessageHandler {
    @MessageMapping(FirstMsgResponse.class)
    public void firstMessage(FirstMsgResponse response, GameClientChannelContext ctx) {
        log.info("收到服务器响应:{}",response.getServerTime());
    }
    @MessageMapping(SecondMsgResponse.class)
    public void secondMessage(SecondMsgResponse response,GameClientChannelContext ctx) {
        log.info("second msg response :{}",response.getBodyObj().getResult1());
    }
}
