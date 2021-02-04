package com.hzoom.core.datasource.anatation;

import com.hzoom.core.datasource.enums.DataSourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    String value() ;

    /**
     * 数据源类型
     * @return
     */
    DataSourceType type() default DataSourceType.AUTO;

}
