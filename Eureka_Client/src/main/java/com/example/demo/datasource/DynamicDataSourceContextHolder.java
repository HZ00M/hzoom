package com.example.demo.datasource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicDataSourceContextHolder {
    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<DynamicDataSourceName> NAME_CONTEXT_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<DynamicDataSourceType> TYPE_CONTEXT_HOLDER = new ThreadLocal<>();
    private static final DynamicDataSourceName DEFAULT_DB_NAME = DynamicDataSourceName.CLOUD;

    /**
     * 设置数据源的名称
     */
    public static void putDataSourceName(DynamicDataSourceName dataSource) {
        log.info("切换到{}数据源", dataSource);
        NAME_CONTEXT_HOLDER.set(dataSource);
    }

    /**
     * 设置数据源的类型
     */
    public static void putDataSourceType(DynamicDataSourceType dynamicDataSourceType) {
        log.info("切换到{}数据源", dynamicDataSourceType);
        TYPE_CONTEXT_HOLDER.set(dynamicDataSourceType);
    }

    /**
     * 获得数据源的名称
     */
    public static DynamicDataSourceName getDataSourceName() {
        return NAME_CONTEXT_HOLDER.get();
    }

    /**
     * 获得数据源的类型
     */
    public static DynamicDataSourceType getDataSourceType() {
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
     * 获取数据库源变量
     *
     * @return
     */
    public static String getDataSource() {
        if (null == getDataSourceName()) {
            return DEFAULT_DB_NAME + "" + getDataSourceType();
        }
        return getDataSourceName() + "" + getDataSourceType();
    }
}
