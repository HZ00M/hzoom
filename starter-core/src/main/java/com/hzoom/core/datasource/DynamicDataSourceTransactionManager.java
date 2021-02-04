package com.hzoom.core.datasource;

import com.hzoom.core.datasource.enums.DataSourceType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import javax.sql.DataSource;

/**
 * 动态数据源事务管理器
 */
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    public DynamicDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 只读事务到读库，读写事务到写库
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {

        //设置数据源
        boolean readOnly = definition.isReadOnly();
        if(readOnly) {
            DynamicDataSourceContextHolder.putDataSourceType(DataSourceType.READ);
        } else {
            DynamicDataSourceContextHolder.putDataSourceType(DataSourceType.WRITE);
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DynamicDataSourceContextHolder.clearDataSource();
    }
}
