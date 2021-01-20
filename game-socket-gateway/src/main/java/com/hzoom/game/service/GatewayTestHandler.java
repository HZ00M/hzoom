package com.hzoom.game.service;

import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.response.FirstMsgResponse;
import com.hzoom.game.message.response.SecondMsgResponse;
import lombok.extern.slf4j.Slf4j;

@MessageHandler
@Slf4j
public class GatewayTestHandler {
    @MessageMapping(FirstMsgResponse.class)
    public void first(FirstMsgResponse response){
        log.info("response {}",response.toString());
    }

    @MessageMapping(SecondMsgResponse.class)
    public void second(SecondMsgResponse response){
        log.info("response {}",response.toString());
    }
}
