package com.example.demo.datasource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicDataSourceContextHolder {
    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     *  所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<DynamicDataSourceGlobal> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void putDataSource(DynamicDataSourceGlobal dataSource)
    {
        log.info("切换到{}数据源", dataSource);
        CONTEXT_HOLDER.set(dataSource);
    }

    /**
     * 获得数据源的变量
     */
    public static DynamicDataSourceGlobal getDataSource()
    {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSource()
    {
        CONTEXT_HOLDER.remove();
    }
}
