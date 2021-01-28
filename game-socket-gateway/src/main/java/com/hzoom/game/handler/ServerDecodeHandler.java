package com.hzoom.game.handler;

import com.hzoom.game.message.common.IMessage;
import com.hzoom.game.utils.AESUtils;
import com.hzoom.game.utils.CompressUtil;
import com.hzoom.game.message.common.MessagePackage;
import com.hzoom.game.utils.JWTUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * MessagePackage解码器
 * 解码客户端消息,协议格式为：消息总长度(int 4) + 消息序列号(int 4) + 消息号(int 4) + 服务ID（short2）+ 客户端发送时间(long 8)
 * + 版本号(int 4) + 是否压缩(byte 1) + body（变长）
 */
public class ServerDecodeHandler extends ChannelInboundHandlerAdapter {
    @Setter
    @Getter
    private String aesSecret; //对称加密密钥
    @Setter
    @Getter
    private JWTUtil.TokenBody tokenBody;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            int messageSize = byteBuf.readInt();
            int clientSeqId = byteBuf.readInt();
            int messageId = byteBuf.readInt();
            int serviceId = byteBuf.readShort();
            long clientSendTime = byteBuf.readLong();
            int version = byteBuf.readInt();
            int compress = byteBuf.readByte();
            byte[] body = null;
            if (byteBuf.readableBytes() > 0) {
                body = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(body);
                if (aesSecret != null && messageId != 1) {//密钥不为空且不是认证消息，对消息体解密
                    body = AESUtils.decode(aesSecret, body);
                }
                if (compress == 1) {
                    body = CompressUtil.decompress(body);
                }
            }
            IMessage.Header header = new IMessage.Header();
            header.setClientSendTime(clientSendTime);
            header.setClientSeqId(clientSeqId);
            header.setMessageId(messageId);
            header.setServiceId(serviceId);
            header.setMessageSize(messageSize);
            header.setVersion(version);
            if (tokenBody!=null){
                header.setPlayerId(tokenBody.getPlayerId());
            }

            MessagePackage messagePackage = new MessagePackage();
            messagePackage.setHeader(header);
            messagePackage.setBody(body);
            ctx.fireChannelRead(messagePackage);
        } finally {
            ReferenceCountUtil.release(byteBuf);
        }
    }
}
