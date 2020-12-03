package com.hzoom.im.command;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Slf4j
@Data
@Service("CHAT")
public class ChatConsoleCommand implements Command<Scanner> {
    private String toUserId;
    private String message;

    @Override
    public void exec(Scanner scanner) {
        System.out.println("请输入聊天信息，格式为：内容@用户名 ");
        String s = scanner.next();
        String[] array = s.split("@");
        try {
            message = array[0];
            toUserId = array[1];
        }catch (Exception e){
            e.printStackTrace();
            exec(scanner);
        }


        log.info("发送的目标用户:{},发送内容:{}",toUserId,message);
    }

    @Override
    public Type getKey() {
        return Type.CHAT;
    }
}
