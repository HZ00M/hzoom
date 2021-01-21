package com.hzoom.game.handler;

import com.hzoom.message.context.GatewayMessageContext;
import com.hzoom.game.entity.manager.PlayerManager;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.request.FirstMsgRequest;
import com.hzoom.game.message.request.SecondMsgRequest;
import com.hzoom.game.message.response.FirstMsgResponse;
import com.hzoom.game.message.response.SecondMsgResponse;
import lombok.extern.slf4j.Slf4j;

@MessageHandler
@Slf4j
public class TestHandler {
    @MessageMapping(FirstMsgRequest.class)
    public void first(FirstMsgRequest request, GatewayMessageContext<PlayerManager> ctx){
        log.info("first {}",request.toString());
        FirstMsgResponse firstMsgResponse = new FirstMsgResponse();
        firstMsgResponse.setServerTime(System.currentTimeMillis());
        ctx.sendMessage(firstMsgResponse);
    }

    @MessageMapping(SecondMsgRequest.class)
    public void second(SecondMsgRequest request, GatewayMessageContext<PlayerManager> ctx){
        log.info("second {}",request.toString());
        SecondMsgResponse secondMsgResponse = new SecondMsgResponse();
        secondMsgResponse.getBodyObj().setResult1(System.currentTimeMillis());
        secondMsgResponse.getBodyObj().setResult2("请求测试");
        ctx.sendMessage(secondMsgResponse);
    }
}
