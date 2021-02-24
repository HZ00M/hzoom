package com.hzoom.core.datasource;

import com.hzoom.core.datasource.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class DynamicDataSourceContextHolder {
    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> NAME_CONTEXT_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<DataSourceType> TYPE_CONTEXT_HOLDER = new ThreadLocal<>();
    private static String DEFAULT_DB_NAME = "";

    public static void setDefaultDbName(String defaultDbName){
        DEFAULT_DB_NAME = defaultDbName;
    }
    /**
     * 设置数据源的名称
     */
    public static void putDataSourceName(String dataSource) {
        log.info("切换到{}数据源名称", dataSource);
        NAME_CONTEXT_HOLDER.set(dataSource);
    }

    /**
     * 设置数据源的类型
     */
    public static void putDataSourceType(DataSourceType type) {
        log.info("切换到{}数据源类型", type);
        TYPE_CONTEXT_HOLDER.set(type);
    }

    /**
     * 获得数据源的名称
     */
    public static String getDataSourceName() {
        return NAME_CONTEXT_HOLDER.get();
    }

    /**
     * 获得数据源的类型
     */
    public static DataSourceType getDataSourceType() {
        return TYPE_CONTEXT_HOLDER.get();
    }

    /**
     * 清空数据源名称
     */
    public static void clearDataSourceName() {
        NAME_CONTEXT_HOLDER.remove();
    }

    /**
     * 清空数据源类型
     */
    public static void clearDataSourceType() {
        TYPE_CONTEXT_HOLDER.remove();
    }

    /**
     * 情况数据源
     */
    public static void clearDataSource() {
        clearDataSourceName();
        clearDataSourceType();
    }


    /**
     * 获取数据库源变量
     *
     * @return
     */
    public static String getDataSource() {
        if (null == getDataSourceName()) {
            return DEFAULT_DB_NAME + "-" + getDataSourceType();
        }
        return getDataSourceName() + "-" + getDataSourceType();
    }
}
