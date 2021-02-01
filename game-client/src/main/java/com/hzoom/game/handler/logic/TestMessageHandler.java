package com.hzoom.game.handler.logic;

import com.hzoom.game.client.GameClientChannelContext;
import com.hzoom.game.message.bird.GetPlayerByIdMsgResponse;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.response.FirstMsgResponse;
import com.hzoom.game.message.response.SecondMsgResponse;
import com.hzoom.game.message.response.ThirdMsgResponse;
import lombok.extern.slf4j.Slf4j;

@MessageHandler
@Slf4j
public class TestMessageHandler {
    @MessageMapping(FirstMsgResponse.class)
    public void firstMessage(FirstMsgResponse response, GameClientChannelContext ctx) {
        log.info("first msg response :{}", response.getServerTime());
    }

    @MessageMapping(SecondMsgResponse.class)
    public void secondMessage(SecondMsgResponse response, GameClientChannelContext ctx) {
        log.info("second msg response :{}", response.getBodyObj().getResult1());
    }

    @MessageMapping(ThirdMsgResponse.class)
    public void thirdMessage(ThirdMsgResponse response, GameClientChannelContext ctx) {
        log.info("third msg response:{}", response.getResponse().getValue1());
    }

    @MessageMapping(GetPlayerByIdMsgResponse.class)
    public void getPlayerByIdMsgResponse(GetPlayerByIdMsgResponse response, GameClientChannelContext ctx) {
        log.info("getPlayerByIdMsgResponse msg response :{}", response.getBodyObj());
    }
}
