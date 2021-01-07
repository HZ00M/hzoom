package com.hzoom.game.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${server.port}")
    private int port;

    @RequestMapping("/https")
    public Object getHtts() {
        return "Hello ,Https" +port;
    }
}
