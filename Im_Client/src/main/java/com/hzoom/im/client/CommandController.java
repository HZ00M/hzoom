package com.hzoom.im.client;

import com.hzoom.core.concurrent.callbackTask.FutureTaskScheduler;
import com.hzoom.im.bean.UserDTO;
import com.hzoom.im.clientSession.ClientSession;
import com.hzoom.im.command.*;
import com.hzoom.im.entity.ImNode;
import com.hzoom.im.entity.LoginBack;
import com.hzoom.im.feign.WebOperator;
import com.hzoom.im.sender.ChatSender;
import com.hzoom.im.sender.LoginSender;
import com.hzoom.im.sender.LogoutSender;
import com.hzoom.im.utils.JsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("CommandController")
public class CommandController implements ApplicationContextAware, SmartInitializingSingleton {
    private ApplicationContext context;

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatSender chatSender;
    @Autowired
    private LoginSender loginSender;
    @Autowired
    private LogoutSender logoutSender;

    private ClientSession session;

    private Channel channel;

    private Map<String, Command> commandMap;

    private Scanner scanner;

    private boolean connectFlag = false;

    private UserDTO user;

    private GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.info(new Date() + ": 连接已经断开……");
        channel = f.channel();
        ClientSession session =
                channel.attr(ClientSession.SESSION_KEY).get();
        session.close();

        //唤醒用户线程
        notifyCommandThread();
    };

    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("连接失败!在10s之后准备尝试重连!");
            eventLoop.schedule(() -> chatClient.doConnect(), 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;
            log.info("服务器 连接成功!");
            channel = f.channel();
            session = new ClientSession(channel);
            session.setConnected(true);
            channel.closeFuture().addListener(closeListener);
            //唤醒用户线程
            notifyCommandThread();
        }
    };

    public void startConnectServer() {
        FutureTaskScheduler.add(() -> {
            chatClient.setConnectedListener(connectedListener);
            chatClient.doConnect();
        });
    }

    public synchronized void waitCommandThread() {
        //休眠，命令收集线程
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyCommandThread() {
        //唤醒，命令收集程
        this.notify();
    }

    private void userLoginAndConnectToServer() {
        //登录
        if (isConnectFlag()) {
            log.info("已经登录成功，不需要重复登录");
            return;
        }

        LoginConsoleCommand command = (LoginConsoleCommand) commandMap.get(Command.Type.LOGIN.toString());
        command.exec(scanner);

        UserDTO user = new UserDTO();
        user.setUserId(command.getUserName());
        user.setToken(command.getPassword());
        user.setDevId("unknown");

        log.info("step1：开始登录WEB GATE");
        LoginBack loginBack = WebOperator.login(command.getUserName(), command.getPassword());
        ImNode imNode = loginBack.getImNode();
        log.info("step1 WEB GATE 返回的node节点是：{}", JsonUtil.pojoToJson(imNode));

        log.info("step2：开始连接Netty 服务节点");
        chatClient.setConnectedListener(connectedListener);
        chatClient.setHost(imNode.getHost());
        chatClient.setPort(imNode.getPort());
        chatClient.doConnect();
        waitCommandThread();
        log.info("step2：Netty 服务节点连接成功");

        log.info("step3：开始登录Netty 服务节点");
        this.user = user;
        session.setUser(user);
        loginSender.setUser(user);
        loginSender.setSession(session);
        loginSender.sendLoginMsg();
        waitCommandThread();

        connectFlag = true;
    }

    public void startCommandThread() throws InterruptedException {
        scanner = new Scanner(System.in);
        while (true) {
            try {
                //建立连接
                while (connectFlag == false) {
                    //输入用户名，然后登录
                    userLoginAndConnectToServer();
                }
                //处理命令
                while (null != session) {
                    ChatConsoleCommand command = (ChatConsoleCommand) commandMap.get(Command.Type.CHAT.toString());
                    command.exec(scanner);
                    startOneChat(command);
                }
            }catch (Exception e){
                log.error("未处理异常");
                LogoutConsoleCommand command = (LogoutConsoleCommand) commandMap.get(Command.Type.LOGOUT.toString());
                command.exec(scanner);
                if (command.isLogout()){
                    startLogout(command);
                }
            }

        }
    }

    //发送单聊消息
    private void startOneChat(ChatConsoleCommand command) {
        //登录
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        chatSender.setSession(session);
        chatSender.setUser(user);
        chatSender.sendTextMsg(command.getToUserId(),command.getMessage());
    }

    public void initCommandMap() {
        commandMap = context.getBeansOfType(Command.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public boolean isConnectFlag() {
        return connectFlag;
    }

    public void setConnectFlag(boolean connectFlag) {
        this.connectFlag = connectFlag;
    }

    public boolean isLogin() {
        if (null == session) {
            log.info("session is null");
            return false;
        }
        return session.isLogin();
    }

    private void startLogout(Command command)throws InterruptedException {
        //登出
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        logoutSender.setUser(user);
        logoutSender.setSession(session);
        logoutSender.sendLogoutMsg();
        connectFlag = false;
        ChannelFuture close = session.close();
        close.sync();
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            initCommandMap();
            startCommandThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
