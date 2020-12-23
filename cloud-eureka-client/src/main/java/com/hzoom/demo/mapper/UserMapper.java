package com.hzoom.demo.mapper;


import com.hzoom.core.sqlgen.GeneralMapper;
import com.hzoom.demo.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends GeneralMapper<User> {
    @Select("select * from t_user where id = #{id}")
    Optional<User> selectById(Integer id);
}
