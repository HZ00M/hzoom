package com.example.demo.datasource;

import org.apache.ibatis.type.Alias;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 自定义多数据源切换注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource
{
    /**
     * 数据源名称
     */
    DataSourceEnum value() default DataSourceEnum.CLOUD;

    /**
     * 数据源类型
     * @return
     */
    DataSourceEnum.Type type() default DataSourceEnum.Type.AUTO;

}
