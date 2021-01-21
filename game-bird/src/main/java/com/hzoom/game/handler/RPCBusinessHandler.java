package com.hzoom.game.handler;

import com.hzoom.game.entity.manager.ArenaManager;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondRPCRequest;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondRPCResponse;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.message.rpc.RPCEvent;
import com.hzoom.message.rpc.RPCEventContext;
import lombok.extern.slf4j.Slf4j;

@MessageHandler
@Slf4j
public class RPCBusinessHandler {

    @RPCEvent(ConsumeDiamondRPCRequest.class)
    public void consumeDiamond(RPCEventContext<ArenaManager> ctx, ConsumeDiamondRPCRequest request) {
         log.debug("收到扣钻石的rpc请求{}",request);
         ConsumeDiamondRPCResponse response = new ConsumeDiamondRPCResponse();
         ctx.sendMessage(response);
    }
}
