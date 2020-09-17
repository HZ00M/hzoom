package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.core.redis.RedisUtils;
import com.example.demo.mapper.ReplyMapper;
import com.example.demo.po.Reply;
import com.example.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReplyService {
    @Autowired
    ReplyMapper replyMapper;

    public Result add(){
        for (int i =0 ;i<10;i++){
            Reply reply = new Reply();
//            reply.setId(i);
            reply.setReplyTime(new Date());
            replyMapper.add(reply);
        }
        return Result.success();
    }
}
