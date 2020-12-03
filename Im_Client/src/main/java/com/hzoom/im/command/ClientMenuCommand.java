package com.hzoom.im.command;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Data
@Service("CLIENT")
public class ClientMenuCommand implements Command<Scanner>{
    private String allCommandsShow;
    private String commandInput;

    @Override
    public void exec(Scanner scanner) {
        System.err.println("请输入某个操作指令：");
        System.err.println(allCommandsShow);
        //  获取第一个指令
        commandInput = scanner.next();
    }

    @Override
    public Type getKey() {
        return Type.CLIENT;
    }
}
