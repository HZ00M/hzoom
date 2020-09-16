package com.example.demo.mapper;

import com.example.core.sqlgen.GeneralMapper;
import com.example.demo.po.Reply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyMapper extends GeneralMapper<Reply> {
}
