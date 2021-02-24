package com.hzoom.core.datasource.interceptor;

import com.hzoom.core.datasource.DynamicDataSourceContextHolder;
import com.hzoom.core.datasource.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class
        }),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
        })
})
@Slf4j
public class DynamicInterceptor implements Interceptor {
    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";
    private static final Map<String, DataSourceType> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //>当通过没有@Transactional注释的Spring服务调用时，它返回false
        //>当使用@Transactional通过Spring服务调用时，它返回true
        boolean synchronizationActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!synchronizationActive) {
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];
            DataSourceType dynamicDataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
            if ((dynamicDataSourceType ==null || dynamicDataSourceType.equals(DataSourceType.AUTO)) && (dynamicDataSourceType = cacheMap.get(ms.getId())) == null) {
                //读方法
                if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                    //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
                    if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                        dynamicDataSourceType = DataSourceType.WRITE;
                    } else {
                        BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                        String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                        if (sql.matches(REGEX)) {
                            dynamicDataSourceType = DataSourceType.WRITE;
                        } else {
                            dynamicDataSourceType = DataSourceType.READ;
                        }
                    }
                } else {
                    dynamicDataSourceType = DataSourceType.WRITE;
                }
                log.warn("设置方法[{}] use [{}] Strategy, SqlCommandType [{}]..", ms.getId(), dynamicDataSourceType.name(), ms.getSqlCommandType().name());
                cacheMap.put(ms.getId(), dynamicDataSourceType);
            }
            DynamicDataSourceContextHolder.putDataSourceType(dynamicDataSourceType);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        //
    }
}
