package com.hzoom.game.handler.codec;

import com.hzoom.game.message.message.DefaultMessageHeader;
import com.hzoom.game.message.message.MessagePackage;
import com.hzoom.game.utils.AESUtils;
import com.hzoom.game.utils.CompressUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private String aesSecretKey;//aes对称秘钥

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        
        try {
            int messageSize = byteBuf.readInt();
            int clientSeqId = byteBuf.readInt();
            int messageId = byteBuf.readInt();
            long serverSendTime = byteBuf.readLong();
            int version = byteBuf.readInt();
            int compress = byteBuf.readByte();
            int errorCode = byteBuf.readInt();
            byte[] body = null;
            if (errorCode==0&&byteBuf.readableBytes()>0){
                body = new byte[byteBuf.readableBytes()];
                if (aesSecretKey!=null&& messageId!=1){
                    body = AESUtils.decode(aesSecretKey,body);
                }
                if (compress==1){
                    body = CompressUtil.decompress(body);
                }
            }
            DefaultMessageHeader header = new DefaultMessageHeader();
            header.setMessageSize(messageSize);
            header.setClientSeqId(clientSeqId);
            header.setMessageId(messageId);
            header.setServerSendTime(serverSendTime);
            header.setVersion(version);
            header.setErrorCode(errorCode);

            MessagePackage messagePackage = new MessagePackage();
            messagePackage.setHeader(header);
            messagePackage.read(body);

            log.info("接收服务器消息,大小：{}:<-{}", messageSize, header);
            ctx.fireChannelRead(messagePackage);
        }finally {
            ReferenceCountUtil.release(byteBuf);
        }
    }

    public void setAesSecretKey(String aesSecretKey) {
        this.aesSecretKey = aesSecretKey;
    }
}
