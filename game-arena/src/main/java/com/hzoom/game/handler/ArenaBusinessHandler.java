package com.hzoom.game.handler;

import com.hzoom.message.context.GatewayMessageContext;
import com.hzoom.message.context.UserEvent;
import com.hzoom.message.context.UserEventContext;
import com.hzoom.game.entity.manager.ArenaManager;
import com.hzoom.game.error.ArenaError;
import com.hzoom.game.message.bird.BuyArenaChallengeTimesMsgRequest;
import com.hzoom.game.message.bird.BuyArenaChallengeTimesMsgResponse;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondRPCRequest;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondRPCResponse;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.common.IMessage;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@MessageHandler
@Slf4j
public class ArenaBusinessHandler {
    @UserEvent(IdleStateEvent.class)
    public void idleStateEvent(UserEventContext<ArenaManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        log.debug("收到空闲事件：{}", event.getClass().getName());
        ctx.getCtx().close();
    }

    @MessageMapping(BuyArenaChallengeTimesMsgRequest.class)
    public void buyTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<ArenaManager> ctx){
        // 先通过rpc扣除钻石，扣除成功之后，再添加挑战次数
        BuyArenaChallengeTimesMsgResponse response = new BuyArenaChallengeTimesMsgResponse();
        DefaultPromise<IMessage> rpcPromise = ctx.newRPCPromise();
        rpcPromise.addListener((Future<IMessage> f)->{
            if (f.isSuccess()){
                ConsumeDiamondRPCResponse rpcResponse = (ConsumeDiamondRPCResponse)f.get();
                int errorCode = rpcResponse.getHeader().getErrorCode();
                if (errorCode==0){
                    ctx.getDataManager().addChallengeTimes(10);
                    log.info("购买竞技场次数成功");
                }else{
                    response.getHeader().setErrorCode(errorCode);
                }
            }else{
                response.getHeader().setErrorCode(ArenaError.SERVER_ERROR.getErrorCode());
            }
            ctx.sendMessage(response);
        });
        ConsumeDiamondRPCRequest rpcRequest = new ConsumeDiamondRPCRequest();
        rpcRequest.getBodyObj().setCount(20);// 假设是20钻石
        ctx.sendRPCMessage(rpcRequest, rpcPromise);
    }
}
