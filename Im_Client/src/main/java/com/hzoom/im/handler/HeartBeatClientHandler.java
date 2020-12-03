package com.hzoom.im.handler;

import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.clientSession.ClientSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

@ChannelHandler.Sharable
@Service
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 100;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        ClientSession session = ClientSession.getSession(ctx);
        UserDTO user = session.getUser();
    }
}
