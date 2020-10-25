package com.example.demo.websocket;

import com.alibaba.fastjson.JSONObject;
import com.example.core.netty.web.annotation.PathVariable;
import com.example.core.netty.web.annotation.RequestParam;
import com.example.core.netty.web.annotation.ServerEndpoint;
import com.example.core.netty.web.annotation.ServerMethod;
import com.example.core.netty.web.core.Session;
import com.example.demo.po.User;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


@Slf4j
@ServerEndpoint(host = "${netty-websocket.host}", path = "${netty-websocket.path}", port = "${netty-websocket.port}",bossLoopGroupThreads = "1",workerLoopGroupThreads = "10")
@Component
public class MyWebSocket {

    @ServerMethod(ServerMethod.Type.BeforeHandshake)
    public void beforeHandshake(Session session, HttpHeaders headers, @RequestParam Long connectTime, @PathVariable String path) {
        log.info("receive beforeHandshake : {}",JSONObject.toJSONString(headers));
    }

    @ServerMethod(ServerMethod.Type.OnOpen)
    public void onOpen(Session session, HttpHeaders headers, @RequestParam Long connectTime, @PathVariable String path) {
        log.info("receive OnOpen : {}",JSONObject.toJSONString(headers));
    }

    @ServerMethod(ServerMethod.Type.OnClose)
    public void onClose(Session session) throws IOException, InterruptedException {
        ChannelFuture close = session.close();
        close.sync();
        log.info("receive OnClose");
    }

    @ServerMethod(ServerMethod.Type.OnError)
    public void onError(Session session, Throwable throwable) {
        if (session.isOpen()) {
            session.close();
        }
        log.info("receive OnError : {}", throwable.getMessage());
    }

    @ServerMethod(ServerMethod.Type.OnMessage)
    public void OnMessage(Session session, String message, @RequestParam Long connectTime, @PathVariable String path, User user) {

        log.info("receive OnMessage : {}", message);
        System.out.println(message);


    }

    @ServerMethod(ServerMethod.Type.OnBinary)
    public void OnBinary(Session session, byte[] message) throws UnsupportedEncodingException {
        String str = new String(message, "UTF-8");
        log.info("receive OnBinary : {}",str);
    }

    @ServerMethod(ServerMethod.Type.OnEvent)
    public void onEvent(Session session, Object evt) {
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
