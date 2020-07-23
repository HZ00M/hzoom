package com.example.core.sqlgen;


import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 通用mapper
 * @param <T>
 */
public interface GeneralMapper<T>{
    @InsertProvider(type = SQLGen.class,method = "insert")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int save(T t);

    @DeleteProvider(type = SQLGen.class,method = "delete")
    int del(T t);

    @UpdateProvider(type = SQLGen.class,method = "update")
    int update(T t);

    @SelectProvider(type = SQLGen.class,method = "select")
    List<T> list(T t);
}
