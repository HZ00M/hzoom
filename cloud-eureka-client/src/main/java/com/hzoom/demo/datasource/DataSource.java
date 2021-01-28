package com.hzoom.demo.datasource;

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
    DataSourceEnum value() default DataSourceEnum.CLOUD0;

    /**
     * 数据源类型
     * @return
     */
    DataSourceEnum.Type type() default DataSourceEnum.Type.AUTO;

}
