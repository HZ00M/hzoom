package com.hzoom.demo.mapper;

import com.hzoom.core.sqlgen.GeneralMapper;
import com.hzoom.demo.po.Reply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyMapper extends GeneralMapper<Reply> {
}
