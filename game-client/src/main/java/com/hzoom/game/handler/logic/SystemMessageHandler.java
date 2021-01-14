package com.hzoom.game.handler.logic;

import com.hzoom.game.client.GameClientChannelContext;
import com.hzoom.game.config.GameClientProperties;
import com.hzoom.game.handler.codec.ClientDecodeHandler;
import com.hzoom.game.handler.codec.ClientEncodeHandler;
import com.hzoom.game.handler.common.HeartbeatHandler;
import com.hzoom.game.message.dispatcher.MessageHandler;
import com.hzoom.game.message.dispatcher.MessageMapping;
import com.hzoom.game.message.response.ConfirmMsgResponse;
import com.hzoom.game.message.response.HeartbeatMsgResponse;
import com.hzoom.game.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

@MessageHandler
@Slf4j
public class SystemMessageHandler {
    @Autowired
    private GameClientProperties gameClientProperties;

    @MessageMapping(ConfirmMsgResponse.class)
    public void confirmMsgResponse(ConfirmMsgResponse response, GameClientChannelContext ctx){
        String aesSecretKey = response.getBodyObj().getAesSecretKey();
        byte[] aesSecretBytes = Base64Utils.decodeFromString(aesSecretKey);
        try {
            // 得到明文的aes加密密钥
            byte[] rsaPrivateKeyBytes = Base64Utils.decodeFromString(gameClientProperties.getRsaPrivateKey());
            byte[] aesKeyBytes = RSAUtils.decryptByPrivateKey(aesSecretBytes, rsaPrivateKeyBytes);
            String aesKey = new String(aesKeyBytes);

            //将对称秘钥交给编解码器
            ClientDecodeHandler clientDecodeHandler = ctx.getChannel().pipeline().get(ClientDecodeHandler.class);
            ClientEncodeHandler clientEncodeHandler = ctx.getChannel().pipeline().get(ClientEncodeHandler.class);
            clientDecodeHandler.setAesSecretKey(aesKey);
            clientEncodeHandler.setAesSecretKey(aesKey);

            //连接确认
            HeartbeatHandler heartbeatHandler = ctx.getChannel().pipeline().get(HeartbeatHandler.class);
            heartbeatHandler.setConfirmSuccess(true);;

            log.info("连接认证成功,channelId:{}",ctx.getChannel().id().asShortText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping(HeartbeatMsgResponse.class)
    public void heartbeatMsgResponse(HeartbeatMsgResponse response,GameClientChannelContext ctx){
        log.info("服务器心跳返回，当前服务器时间：{}",response.getBodyObj().getServerTime());
    }
}
