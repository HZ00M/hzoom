package com.hzoom.im.command;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@Data
@Component("LOGOUT")
public class LogoutConsoleCommand implements Command<Scanner>{
    private boolean logout;
    @Override
    public void exec(Scanner scanner) {
        System.out.println("确认退出请按 Y ");
        String s = scanner.next();
        String confirm = s;

        logout = confirm.equalsIgnoreCase("Y");
    }

    @Override
    public Type getKey() {
        return Type.LOGOUT;
    }
}
