package com.hzoom.game.handler;

import com.hzoom.game.entity.manager.ArenaManager;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondMsgRequest;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondMsgResponse;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.rpc.RPCEvent;
import com.hzoom.game.rpc.RPCEventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageHandler
public class RPCBusinessHandler {
    private Logger logger = LoggerFactory.getLogger(RPCBusinessHandler.class);
    @RPCEvent(ConsumeDiamondMsgRequest.class)
    public void consumDiamond(RPCEventContext<ArenaManager> ctx, ConsumeDiamondMsgRequest request) {
         logger.debug("收到扣钻石的rpc请求");
         ConsumeDiamondMsgResponse response = new ConsumeDiamondMsgResponse();
         ctx.sendMessage(response);
    }
}
