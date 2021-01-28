package com.hzoom.message.service;

import com.hzoom.game.message.common.MessagePackage;
import com.hzoom.message.stream.GatewaySink;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

@Slf4j
public class GatewayMessageManager{
    private Map<Long, Channel> playerChannelMap = new HashMap<>();// playerId与Netty Channel的映射容器，这里使用的是HashMap，所以，对于Map的操作都要放在锁里面
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();// 读写锁,使用非公平锁

    @StreamListener(GatewaySink.gateway)
    public void receive(byte[] payload) {
        MessagePackage messagePackage = MessagePackage.readMessagePackage(payload);
        long playerId = messagePackage.getHeader().getPlayerId();
        Channel channel = getChannel(playerId);
        if (channel != null) {
            channel.writeAndFlush(messagePackage);
        }
    }

    public void addChannel(Long playerId, Channel channel) {
        writeLock(() -> playerChannelMap.put(playerId, channel));
    }

    public Channel getChannel(Long playerId) {
        return readLock(() -> playerChannelMap.get(playerId));
    }

    public void removeChannel(Long playerId,Channel removeChannel){
        writeLock(()->{
            Channel existChannel = playerChannelMap.get(playerId);
            if (existChannel!=null && removeChannel == existChannel){// 必须是同一个对象才可以移除
                playerChannelMap.remove(playerId);
                existChannel.close();
            }
        });
    }

    public void broadcast(BiConsumer<Long,Channel> consumer){
        readLock(()->{
            playerChannelMap.forEach(consumer);
            return null;
        });
    }

    public int getChannelCount(){
        return readLock(()->playerChannelMap.size());
    }

    private <T> T readLock(Callable<T> task) {//封装添加读锁，统一添加，防止写错
        lock.readLock().lock();
        try {
            return task.call();
        } catch (Exception e) {
            log.error("PlayerChannelManager读锁处理异常！message : {}", e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    private void writeLock(Runnable task) {//封装添加写锁，统一添加，防止写错
        lock.writeLock().lock();
        try {
            task.run();
        } catch (Exception e) {
            log.error("PlayerChannelManager写锁处理异常！message : {}", e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

}
