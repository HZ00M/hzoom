package com.example.demo.mapper;


import com.example.core.sqlgen.GeneralMapper;
import com.example.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends GeneralMapper<User> {
}
