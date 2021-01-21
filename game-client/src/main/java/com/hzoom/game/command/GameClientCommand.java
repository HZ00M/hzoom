package com.hzoom.game.command;

import com.hzoom.game.client.GameClientBoot;
import com.hzoom.game.client.GameClientInitService;
import com.hzoom.game.config.GameClientProperties;
import com.hzoom.game.http.request.SelectGameGatewayParam;
import com.hzoom.game.http.response.GameGatewayInfoResponse;
import com.hzoom.game.message.bird.BuyArenaChallengeTimesMsgRequest;
import com.hzoom.game.message.bird.EnterGameMsgRequest;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.request.ConfirmMsgRequest;
import com.hzoom.game.message.request.FirstMsgRequest;
import com.hzoom.game.message.request.SecondMsgRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@Slf4j
public class GameClientCommand {
    @Autowired
    private GameClientBoot gameClientBoot;
    @Autowired
    private GameClientProperties gameClientProperties;
    @Autowired
    private GameClientInitService clientInitService;

    @ShellMethod("获取网关服务器，格式：g  [userId] [playId] [openId] [zoneId]")
    public void g(@ShellOption(defaultValue = "0") Long userId, @ShellOption(defaultValue = "0") Long playId, @ShellOption(defaultValue = "123456") String openId, @ShellOption(defaultValue = "1") String zoneId) {
        if (userId == 0 || playId == 0) {
            log.error("请输入userId和playId获取网关信息");
            return;
        }
        SelectGameGatewayParam param = new SelectGameGatewayParam();
        param.setOpenId(openId);
        param.setPlayerId(playId);
        param.setUserId(userId);
        param.setZoneId(zoneId);
        GameGatewayInfoResponse response = clientInitService.selectGateway(param);
        if (response != null) {
            log.info("网关信息 {}", response.toString());
        }
    }

    @ShellMethod("连接服务器，格式：c  [host] [port]")
    public void c(@ShellOption(defaultValue = "") String host, @ShellOption(defaultValue = "0") int port) {
        if (!host.isEmpty()) {
            if (port == 0) {
                log.error("请输入服务器端口号");
                return;
            }
            gameClientProperties.setDefaultGameGatewayHost(host);
            gameClientProperties.setDefaultGameGatewayPort(port);
        }
        gameClientBoot.launch();
    }

    @ShellMethod("关闭连接")
    public void close() {
        gameClientBoot.getChannel().close();
    }

    @ShellMethod("发送测试消息，格式：s 消息号")
    public void s(int messageId) {
        if (messageId == 1) {//发送认证请求
            ConfirmMsgRequest request = new ConfirmMsgRequest();
            request.getBodyObj().setToken(gameClientProperties.getGatewayToken());
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if (messageId == 10001) {
            // 向服务器发送一条消息
            FirstMsgRequest request = new FirstMsgRequest();
            request.setValue("Hello,server !!");
            IMessage.Header header = request.getHeader();
            header.setClientSendTime(System.currentTimeMillis());
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if (messageId == 10002) {
            SecondMsgRequest request = new SecondMsgRequest();
            request.getBodyObj().setValue1("你好，这是测试请求");
            request.getBodyObj().setValue2(System.currentTimeMillis());
            request.getBodyObj().setValue3("System.currentTimeMillis()");
            gameClientBoot.getChannel().writeAndFlush(request);
        }
//        if(messageId == 10003) {
//            ThirdMsgRequest request = new ThirdMsgRequest();
//            ThirdMsgRequestBody requestBody = ThirdMsgRequestBody.newBuilder().setValue1("我是Protocol Buffer序列化的").setValue2(System.currentTimeMillis()).build();
//            request.setRequestBody(requestBody);
//            gameClientBoot.getChannel().writeAndFlush(request);
//        }
        if(messageId == 201) {//进入游戏请求
            EnterGameMsgRequest request = new EnterGameMsgRequest();
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if(messageId == 202) {
            BuyArenaChallengeTimesMsgRequest request = new BuyArenaChallengeTimesMsgRequest();
            gameClientBoot.getChannel().writeAndFlush(request);
        }
    }
}
