package com.hzoom.game.command;

import com.hzoom.game.client.GameClientBoot;
import com.hzoom.game.client.GameClientInitService;
import com.hzoom.game.config.GameClientProperties;
import com.hzoom.game.http.GameCenterApi;
import com.hzoom.game.http.TokenInterceptor;
import com.hzoom.game.http.common.BaseResponse;
import com.hzoom.game.http.request.CreatePlayerParam;
import com.hzoom.game.http.request.LoginParam;
import com.hzoom.game.http.request.SelectGameGatewayParam;
import com.hzoom.game.http.response.LoginResponse;
import com.hzoom.game.http.response.SelectGameGatewayResponse;
import com.hzoom.game.http.response.ZonePlayerInfoResponse;
import com.hzoom.game.message.bird.BuyArenaChallengeTimesMsgRequest;
import com.hzoom.game.message.bird.EnterGameMsgRequest;
import com.hzoom.game.message.bird.GetPlayerByIdMsgRequest;
import com.hzoom.game.message.message.IMessage;
import com.hzoom.game.message.request.ConfirmMsgRequest;
import com.hzoom.game.message.request.FirstMsgRequest;
import com.hzoom.game.message.request.SecondMsgRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@Slf4j
public class GameClientCommand implements ApplicationContextAware {
    @Autowired
    private GameClientBoot gameClientBoot;
    @Autowired
    private GameClientProperties gameClientProperties;
    @Autowired
    private GameClientInitService clientInitService;
    @Autowired
    private GameCenterApi gameCenterApi;

    private ApplicationContext applicationContext;
    @ShellMethod("登录，格式：login [openId] [sdkToken]")
    public void login(@ShellOption(defaultValue = "") String openId,@ShellOption(defaultValue = "") String sdkToken) {
        if (openId.isEmpty()||sdkToken.isEmpty()){
            log.error("缺少登录参数");
            return;
        }
        LoginParam param = new LoginParam();
        param.setOpenId(openId);
        param.setSdkToken(sdkToken);
        BaseResponse<LoginResponse> response = gameCenterApi.login(param);
        if (response != null) {
            log.info("网关信息 {}", response.toString());
        }
        String webToken = response.getData().getToken();
        gameClientProperties.setWebToken(webToken);
    }

    @ShellMethod("创建角色，格式：create  [nikeName] [zoneId]")
    public void create(@ShellOption(defaultValue = "") String nikeName,  @ShellOption(defaultValue = "1") String zoneId) {
        if (nikeName.isEmpty()) {
            log.error("请输入nikeName创建角色");
            return;
        }
        CreatePlayerParam param = new CreatePlayerParam();
        param.setNickName(nikeName);
        param.setZoneId(zoneId);
        BaseResponse<ZonePlayerInfoResponse> response = gameCenterApi.createPlayer(param);
        if (response != null) {
            log.info("网关信息 {}", response.toString());
        }
    }

    @ShellMethod("获取网关服务器，格式：gateway  [userId] [playId] [openId] [zoneId]")
    public void gateway(@ShellOption(defaultValue = "0") Long userId, @ShellOption(defaultValue = "0") Long playId, @ShellOption(defaultValue = "123456") String openId, @ShellOption(defaultValue = "1") String zoneId) {
        if (userId == 0 || playId == 0) {
            log.error("请输入userId和playId获取网关信息");
            return;
        }
        SelectGameGatewayParam param = new SelectGameGatewayParam();
        param.setOpenId(openId);
        param.setPlayerId(playId);
        param.setUserId(userId);
        param.setZoneId(zoneId);
        SelectGameGatewayResponse response = clientInitService.selectGateway(param);
        if (response != null) {
            log.info("网关信息 {}", response.toString());
        }
    }

    @ShellMethod("连接服务器，格式：conn  [host] [port]")
    public void conn(@ShellOption(defaultValue = "") String host, @ShellOption(defaultValue = "0") int port) {
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

    @ShellMethod("关闭连接 close")
    public void close() {
        gameClientBoot.getChannel().close();
    }

    @ShellMethod("发送测试消息，格式：send 消息号 参数")
    public void send(int messageId,@ShellOption(defaultValue = "0")String... param) {
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
        if(messageId == 210) {//购买次数请求
            BuyArenaChallengeTimesMsgRequest request = new BuyArenaChallengeTimesMsgRequest();
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if (messageId == 202){//获取玩家信息请求
            GetPlayerByIdMsgRequest request = new GetPlayerByIdMsgRequest();
            request.getBodyObj().setPlayerId(Integer.valueOf(param[0]));
            gameClientBoot.getChannel().writeAndFlush(request);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
