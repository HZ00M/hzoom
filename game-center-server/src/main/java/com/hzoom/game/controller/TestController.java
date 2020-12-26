package com.hzoom.game.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {


    @RequestMapping("/https")
    public Object getHtts() {
        return "Hello ,Https";
    }
}
