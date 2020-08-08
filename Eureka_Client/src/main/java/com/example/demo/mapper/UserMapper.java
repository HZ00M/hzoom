package com.example.demo.mapper;


import com.example.core.sqlgen.GeneralMapper;
import com.example.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends GeneralMapper<User> {
    @Select("select * from t_user where id = #{id}")
    Optional<User> selectById(Integer id);
}
