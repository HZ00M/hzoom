package com.hzoom.game.handler;

import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.utils.AESUtils;
import com.hzoom.game.utils.CompressUtil;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.server.GatewayServerProperties;
import com.hzoom.game.utils.JWTUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import lombok.Setter;

/**
 * MessagePackage编码器
 * 编码服务器向客户端发送的数据协议格式为：消息总长度(int 4)  + 消息序列号(int 4) + 消息号(int 4) + 服务端发送时间(long 8)
 * + 版本号(int 4) + 是否压缩(byte 1) + 错误码(int 4) + body（变长）
 */
public class ServerEncodeHandler extends MessageToByteEncoder<MessagePackage> {
    private static final int MESSAGE_HEADER_LEN = 29;
    @Setter
    @Getter
    private String aesSecret;// 对称加密密钥
    private GatewayServerProperties serverProperties;

    public ServerEncodeHandler(GatewayServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePackage msg, ByteBuf out) throws Exception {
        int messageSize = MESSAGE_HEADER_LEN;
        byte[] body = msg.body();
        int compress = 0;
        if (body != null) {// 达到压缩条件，进行压缩
            if (body.length >= serverProperties.getCompressMessageSize()) {
                body = CompressUtil.compress(body);
                compress = 1;
            }
            if (aesSecret != null && msg.getHeader().getMessageId() != 1) {
                body = AESUtils.encode(aesSecret, body);
            }
            messageSize += body.length;
        }
        IMessage.Header header = msg.getHeader();
        out.writeInt(messageSize);
        out.writeInt(header.getClientSeqId());
        out.writeInt(header.getMessageId());
        out.writeLong(System.currentTimeMillis());
        out.writeInt(header.getVersion());
        out.writeByte(compress);
        out.writeInt(header.getErrorCode());
        if (body != null) {
            out.writeBytes(body);
        }
    }
}
