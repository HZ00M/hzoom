package com.hzoom.game.handler;

import com.hzoom.game.context.GatewayMessageContext;
import com.hzoom.game.context.UserEvent;
import com.hzoom.game.context.UserEventContext;
import com.hzoom.game.entity.Player;
import com.hzoom.game.entity.manager.PlayerManager;
import com.hzoom.game.event.GetArenaPlayerEvent;
import com.hzoom.game.event.GetPlayerInfoEvent;
import com.hzoom.game.message.bird.*;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondMsgRequest;
import com.hzoom.game.message.bird.rpc.ConsumeDiamondMsgResponse;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.message.IMessage;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@MessageHandler
public class PlayerLogicHandler {
    private Logger logger = LoggerFactory.getLogger(PlayerLogicHandler.class);

    @UserEvent(IdleStateEvent.class)
    public void idleStateEvent(UserEventContext<PlayerManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        logger.debug("收到空闲事件：{}", event.getClass().getName());
        ctx.getCtx().close();
    }

    @UserEvent(GetPlayerInfoEvent.class)
    public void getPlayerInfoEvent(UserEventContext<PlayerManager> ctx, GetPlayerInfoEvent event, Promise<Object> promise) {
        GetPlayerByIdMsgResponse response = new GetPlayerByIdMsgResponse();
        Player player = ctx.getDataManager().getPlayer();
        response.getBodyObj().setPlayerId(player.getPlayerId());
        response.getBodyObj().setNickName(player.getNickName());
        promise.setSuccess(response);
    }

    @MessageMapping(EnterGameMsgRequest.class)
    public void enterGame(EnterGameMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        logger.info("接收到客户端进入游戏请求：{}", request.getHeader().getPlayerId());
        EnterGameMsgResponse response = new EnterGameMsgResponse();
        response.getBodyObj().setNickname("叶孤城");
        response.getBodyObj().setPlayerId(1);
        ctx.sendMessage(response);
    }


    @MessageMapping(GetPlayerByIdMsgRequest.class)
    public void getPlayerById(GetPlayerByIdMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = request.getBodyObj().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetPlayerInfoEvent event = new GetPlayerInfoEvent(playerId);
        ctx.sendUserEvent(event, promise, playerId).addListener(new GenericFutureListener<Future<? super Object>>() {
            @Override
            public void operationComplete(Future<? super Object> future) throws Exception {
                if (future.isSuccess()) {
                    GetPlayerByIdMsgResponse response = (GetPlayerByIdMsgResponse) future.get();
                    ctx.sendMessage(response);
                } else {
                    logger.error("playerId {} 数据查询失败", playerId, future.cause());
                }
            }
        });
    }

    @MessageMapping(BuyArenaChallengeTimesMsgRequest.class) // 接收客户端购买竞技场挑战次数的请求
    public void buyArenaChallengeTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        ConsumeDiamondMsgRequest consumeDiamondMsgRequest = new ConsumeDiamondMsgRequest();
        Promise<IMessage> promise = ctx.newPromise();
        promise.addListener(new GenericFutureListener<Future<IMessage>>() {
            @Override
            public void operationComplete(Future<IMessage> future) throws Exception {
                if (future.isSuccess()) {
                    ConsumeDiamondMsgResponse rpcResponse = (ConsumeDiamondMsgResponse) future.get();
                    if(rpcResponse.getHeader().getErrorCode() == 0) {
                        // 如果错码为0，表示扣钻石成功，可以增加挑战次数
                    }
                } else {
                    logger.error("竞技场扣除钻石失败",future.cause());
                    //向客户端返回错误码
                }
            }
        });
        ctx.sendRPCMessage(consumeDiamondMsgRequest, promise);
    }


    private List<Long> getAreanPlayerIdList() {
        return Arrays.asList(2L, 3L, 4L);// 模拟竞技场列表playerId
    }

    @MessageMapping(GetArenaPlayerListMsgRequest.class)
    public void getArenaPlayerList(GetArenaPlayerListMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        List<Long> playerIds = this.getAreanPlayerIdList();// 获取本次要显示的PlayerId
        List<GetArenaPlayerListMsgResponse.ArenaPlayer> arenaPlayers = new ArrayList<>(playerIds.size());
        playerIds.forEach(playerId -> {// 遍历所有的PlayerId，向他们对应的GameChannel发送查询事件
            GetArenaPlayerEvent getArenaPlayerEvent = new GetArenaPlayerEvent(playerId);
            Promise<Object> promise = ctx.newPromise();// 注意，这个promise不能放到for循环外面，一个Promise只能被setSuccess一次。
            ctx.sendUserEvent(getArenaPlayerEvent, promise, playerId).addListener(new GenericFutureListener<Future<? super Object>>() {

                @Override
                public void operationComplete(Future<? super Object> future) throws Exception {
                    if (future.isSuccess()) {// 如果执行成功，获取执行的结果
                        GetArenaPlayerListMsgResponse.ArenaPlayer arenaPlayer = (GetArenaPlayerListMsgResponse.ArenaPlayer) future.get();
                        arenaPlayers.add(arenaPlayer);
                    } else {
                        arenaPlayers.add(null);
                    }
                    if (arenaPlayers.size() == playerIds.size()) {// 如果数量相等，说明所有的事件查询都已执行成功，可以返回给客户端数据了。
                        List<GetArenaPlayerListMsgResponse.ArenaPlayer> result = arenaPlayers.stream().filter(c -> c != null).collect(Collectors.toList());
                        GetArenaPlayerListMsgResponse response = new GetArenaPlayerListMsgResponse();
                        response.getBodyObj().setArenaPlayers(result);
                        ctx.sendMessage(response);
                    }
                }
            });
        });
    }

    @UserEvent(GetArenaPlayerEvent.class)
    public void getArenaPlayer(UserEventContext<PlayerManager> utx, GetArenaPlayerEvent event, Promise<Object> promise) {
        GetArenaPlayerListMsgResponse.ArenaPlayer arenaPlayer = new GetArenaPlayerListMsgResponse.ArenaPlayer();
        Player player = utx.getDataManager().getPlayer();
        arenaPlayer.setPlayerId(player.getPlayerId());
        arenaPlayer.setNickName(player.getNickName());
    }
    
    
    
}
