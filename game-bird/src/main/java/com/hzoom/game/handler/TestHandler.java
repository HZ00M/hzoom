package com.hzoom.game.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hzoom.game.entity.manager.PlayerManager;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.request.FirstMsgRequest;
import com.hzoom.game.message.request.SecondMsgRequest;
import com.hzoom.game.message.request.ThirdMsgRequest;
import com.hzoom.game.message.response.FirstMsgResponse;
import com.hzoom.game.message.response.SecondMsgResponse;
import com.hzoom.game.message.response.ThirdMsgResponse;
import com.hzoom.game.proto.GameProtoMsg;
import com.hzoom.message.context.GatewayMessageContext;
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

    @MessageMapping(ThirdMsgRequest.class)
    public void third(ThirdMsgRequest request, GatewayMessageContext<PlayerManager> ctx) throws InvalidProtocolBufferException {
        GameProtoMsg.FirstBodyRequest firstBodyRequest = GameProtoMsg.FirstBodyRequest.parseFrom(request.body());
        log.info("ThirdMsgRequest：{}",firstBodyRequest);
        ThirdMsgResponse response  = new ThirdMsgResponse();
        GameProtoMsg.FirstBodyResponse firstBodyResponse = GameProtoMsg.FirstBodyResponse.newBuilder().setValue1("第一个protoBuf消息").build();
        response.setResponse(firstBodyResponse);
        ctx.sendMessage(response);
    }
}
