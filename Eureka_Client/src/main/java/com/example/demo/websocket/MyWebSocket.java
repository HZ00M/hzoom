package com.example.demo.websocket;

import com.alibaba.fastjson.JSONObject;
import com.example.core.netty.web.annotation.RequestParam;
import com.example.core.netty.web.annotation.ServerEndpoint;
import com.example.core.netty.web.annotation.ServerListener;
import com.example.core.netty.web.core.WebSocketChannel;
import com.example.core.netty.web.enums.ListenerTypeEnum;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@ServerEndpoint(host = "${netty-websocket.host}", path = "${netty-websocket.path}", port = "${netty-websocket.port}", workerLoopGroupThreads = "10")
@Component
public class MyWebSocket {

    @ServerListener(ListenerTypeEnum.BeforeHandshake)
    public void beforeHandshake(WebSocketChannel webSocketChannel, HttpHeaders headers, @RequestParam Long connectTime) {
        log.info("receive beforeHandshake : {}",JSONObject.toJSONString(headers));
    }

    @ServerListener(ListenerTypeEnum.OnOpen)
    public void onOpen(WebSocketChannel webSocketChannel, HttpHeaders headers, @RequestParam Long connectTime) {
        log.info("receive OnOpen : {}",JSONObject.toJSONString(headers));
    }

    @ServerListener(ListenerTypeEnum.OnClose)
    public void onClose(WebSocketChannel webSocketChannel) throws IOException, InterruptedException {
        ChannelFuture close = webSocketChannel.close();
        close.sync();
        log.info("receive OnClose");
    }

    @ServerListener(ListenerTypeEnum.OnError)
    public void onError(WebSocketChannel webSocketChannel, Throwable throwable) {
        if (webSocketChannel.isOpen()) {
            webSocketChannel.close();
        }
        log.info("receive OnError : {}", throwable.getMessage());
    }

    @ServerListener(ListenerTypeEnum.OnMessage)
    public void OnMessage(WebSocketChannel webSocketChannel, String message) {
        log.info("receive OnMessage : {}", message);
    }

//    @ServerListener(ListenerTypeEnum.OnBinary)
//    public void OnBinary(WebSocketChannel webSocketChannel, String message) {
//        log.info("receive OnBinary : {}", message);
//    }

    @ServerListener(ListenerTypeEnum.OnEvent)
    public void onEvent(WebSocketChannel webSocketChannel, Object evt) {
        log.info("Event monitoring" + JSONObject.toJSONString(evt));
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    log.info("read idle");
                    break;
                case WRITER_IDLE:
                    log.info("write idle");
                    break;
                case ALL_IDLE:
                    log.info("all idle");
                    break;
                default:
                    break;
            }
        }
    }
}
