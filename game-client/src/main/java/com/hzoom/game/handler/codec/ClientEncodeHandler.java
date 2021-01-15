package com.hzoom.game.handler.codec;

import com.hzoom.game.config.GameClientProperties;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.utils.AESUtils;
import com.hzoom.game.utils.CompressUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientEncodeHandler extends MessageToByteEncoder<IMessage> {
    /**
     * 发送消息的包头总长度：即：消息总长度(4) + 客户端消息序列号长度(4) + 消息请求ID长度（4） + 服务ID(2) + 客户端发送时间长度(8) + 协议版本长度(4) +
     * 是否压缩长度(1)
     */
    private static final int GAME_MESSAGE_HEADER_LEN = 27;
    private GameClientProperties gameClientProperties;
    private String aesSecretKey;//aes对称秘钥
    private int clientSeqId;//消息序列号

    @Override
    protected void encode(ChannelHandlerContext ctx, IMessage msg, ByteBuf out) throws Exception {
        int messageSize = GAME_MESSAGE_HEADER_LEN;
        byte[] body = msg.body();
        int compress = 0;//是否压缩
        if (body != null) {
            if (body.length >= gameClientProperties.getMessageCompressSize()) {
                body = CompressUtil.compress(body);
            }
            if (aesSecretKey != null && msg.getHeader().getMessageId() != 1) {
                body = AESUtils.encode(aesSecretKey, body);
            }
            messageSize += body.length;
        }
        IMessage.Header header = msg.getHeader();
        out.writeInt(messageSize);
        out.writeInt(++clientSeqId);
        out.writeInt(header.getMessageId());
        out.writeShort(header.getServiceId());
        out.writeLong(System.currentTimeMillis());
        out.writeInt(gameClientProperties.getVersion());
        out.writeByte(compress);
        if (body!=null){
            out.writeBytes(body);
        }
    }

    public ClientEncodeHandler(GameClientProperties gameClientProperties) {
        this.gameClientProperties = gameClientProperties;
    }

    public void setAesSecretKey(String aesSecretKey) {
        this.aesSecretKey = aesSecretKey;
    }
}
