package com.hzoom.demo.service;

import com.hzoom.demo.mapper.ReplyMapper;
import com.hzoom.demo.po.Reply;
import com.hzoom.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
