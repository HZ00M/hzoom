package com.example.demo.interceptor;

import com.example.core.sqlgen.annotation.Limit;
import com.example.core.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * mybatis 排序、限制插件
 */
//@Component
@Slf4j
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class ReduceInterceptor implements Interceptor {
    /**
     * 插件运行的代码，它将代替原有的方法，要重写最重要的intercept方法
     */

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler delegate = getUnProxyObject(invocation);
        PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler) ReflectUtil.getFieldValue(delegate, "delegate");
        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(preparedStatementHandler, "mappedStatement");
        Limit limitByDelegate = getLimitByDelegate(mappedStatement);
        MetaObject metaObject = SystemMetaObject.forObject(delegate);
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        System.out.println("SQL:----" + sql);
        log.error(sql);
        if (!checkSelect(sql)) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramsObject = boundSql.getParameterObject();

        return invocation.proceed();
    }

    /**
     * 从代理对象分离处真实对象
     */
    private StatementHandler getUnProxyObject(Invocation invocation) {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler  = SystemMetaObject.forObject(statementHandler);
        //1.分离代理对象。由于会形成多次代理，所以需要通过while循环分离出最终被代理的对象，从而方便提取信息。
        Object h = null;
        while (metaStatementHandler.hasGetter("h")) {
            h = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(h);
        }
        //2.获取到代理对象中包含的被代理的真实对象
        Object obj = metaStatementHandler.getValue("target");
        //3.获取被代理对象的MetaObject方便进行信息提取
        metaStatementHandler = SystemMetaObject.forObject(obj);
        while (metaStatementHandler.hasGetter("h")){
            h = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(h);
            obj = metaStatementHandler.getValue("target");
        }
        if (h != null){
            statementHandler = (StatementHandler)obj;
        }

        return statementHandler;
    }

    /**
     * 判断是否是select 语句
     */
    private boolean checkSelect(String select) {
        String trimSql = select.trim();
        int index = trimSql.toLowerCase().indexOf("select");
        return index == 0;
    }

    /**
     * 功能描述:  改写sql以满足分页的需求
     */
    private Object changeSql(Invocation invocation, MetaObject metaObject, BoundSql boundSql, int pageNum, int pageSize) throws Exception {
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        String newSql = "select * from (" + sql + ") $_paging_table limit ?,?";
        /**
         * 关键代码     修改要执行的sql
         */
        metaObject.setValue("delegate.boundSql.sql", newSql);
        //相当于调用StatementHandler的prepare方法，预编译了当前的sql并设置原有的参数，但是少了两个分页参数，它返回的是一个preparedStatement对象

        PreparedStatement ps = (PreparedStatement) invocation.proceed();
        //计算sql总参数个数

        int count = ps.getParameterMetaData().getParameterCount();
        ps.setInt(count - 1, (pageNum - 1) * pageSize);
        ps.setInt(count, pageSize);
        return ps;

    }


    public static Limit getLimitByDelegate(MappedStatement mappedStatement) {
        Limit limit = null;
        try {
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1, id.length());
            final Class cls = Class.forName(className);
            final Method[] method = cls.getMethods();
            for (Method me : method) {
                if (me.getName().equals(methodName) && me.isAnnotationPresent(Limit.class)) {
                    limit = me.getAnnotation(Limit.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return limit;
    }
}
