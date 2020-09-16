package com.example.demo.controller;

import com.example.demo.po.Reply;
import com.example.demo.service.ReplyService;
import com.example.demo.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/sharding")
public class ShardingController {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    ReplyService replyService;

    @RequestMapping("/add")
    public Result add(){
        return replyService.add();
    }
}
