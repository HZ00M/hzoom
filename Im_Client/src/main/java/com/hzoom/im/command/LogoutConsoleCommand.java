package com.hzoom.im.command;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;
@Slf4j
@Service("LOGOUT")
public class LogoutConsoleCommand implements Command<Scanner>{
    @Override
    public void exec(Scanner scanner) {

    }

    @Override
    public Type getKey() {
        return Type.LOGOUT;
    }
}
