package com.example.demo.interceptor;

import com.example.core.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * mybatis分页插件
 */
//@Component
@Slf4j
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class MybatisInterceptor implements Interceptor{

    private int defaultPage;//默认页码
    private int defaultPageSize;//默认每页条数
    private Boolean defaultUseFlag;//是否默认启用
    private Boolean defaultCheckFlag;//默认检查当前页码的正确性

    /**
     * 插件运行的代码，它将代替原有的方法，要重写最重要的intercept方法
     */

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler delegate  = getUnProxyObject(invocation);
        PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler) ReflectUtil.getFieldValue(delegate, "delegate");
        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(preparedStatementHandler, "mappedStatement");
        MetaObject metaObject = SystemMetaObject.forObject(delegate);
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        log.info("SQL:----" + sql);
        log.error(sql);
        if (!checkSelect(sql)) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramsObject = boundSql.getParameterObject();
        PageParam pageParam = getPageParams(paramsObject);
        if (pageParam == null) {
            invocation.proceed();
        }
        Integer pageNum = pageParam.getPage() == 0 ? this.defaultPage : pageParam.getPage();
        Integer pageSize = pageParam.getPageSize() == 0 ? this.defaultPageSize : pageParam.getPageSize();
        Boolean useFlag = pageParam.isUseFlag() == false ? this.defaultUseFlag : pageParam.isUseFlag();
        Boolean checkFlag = pageParam.isCheckFlag() == false ? this.defaultCheckFlag : pageParam.isCheckFlag();
        if (!useFlag) {
            return invocation.proceed();
        }
        int total = getTotal(invocation, metaObject, boundSql);
        setTotalToPageParams(pageParam, total, pageSize);
        checkPage(checkFlag, pageNum, pageParam.getTotalPage());
        return changeSql(invocation, metaObject, boundSql, pageNum, pageSize);
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String strDefaultPage = properties.getProperty("default.page", "1");
        String strDefaultPageSize = properties.getProperty("default.pageSize", "50");
        String strDefaultUseFlag = properties.getProperty("default.useFlag", "false");
        String strDefaultCheckFlag = properties.getProperty("default.checkFlag", "false");
        this.defaultPage = Integer.parseInt(strDefaultPage);
        this.defaultPageSize = Integer.parseInt(strDefaultPageSize);
        this.defaultUseFlag = Boolean.parseBoolean(strDefaultUseFlag);
        this.defaultCheckFlag = Boolean.parseBoolean(strDefaultCheckFlag);
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

    /*
        获取分页参数，这里支持使用Map和@Param注解传递参数，或者POJO继承PageParams，三种方式。
    */
    private PageParam getPageParams(Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }
        PageParam pageParam = null;
        if (parameterObject instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) parameterObject;
            Set<String> keySet = paramMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = paramMap.get(key);
                if (value instanceof PageParam) {
                    return (PageParam) value;
                }
            }
        } else if (parameterObject instanceof PageParam) {
            pageParam = (PageParam) parameterObject;
        }
        return pageParam;
    }

    /**
     * 功能描述:  获取总数
     */
    private int getTotal(Invocation invocation, MetaObject metaObject, BoundSql boundSql) throws Throwable {
        //获取当前的MappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        Configuration cfg = mappedStatement.getConfiguration();
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        String countSql = "select count(0) as total from (" + sql + ") $_paging";
        //获取拦截的方法参数，是Connection对象
        Connection conn = (Connection) invocation.getArgs()[0];
        PreparedStatement preparedStatement = null;
        int total = 0;
        try {
            //追加查询关键代码
            preparedStatement = conn.prepareStatement(countSql);
            BoundSql countBoundSql = new BoundSql(cfg, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBoundSql);
            //设置总数sql参数
            parameterHandler.setParameters(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                total = rs.getInt("total");
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return total;
    }

    /**
     * 功能描述:  判断当前页码是否大于最大页码
     *
     * @return:
     * @since: 1.0.0
     * @Author:dalong
     * @Date:
     */
    private void checkPage(Boolean checkFlag, int pageNum, int totalPage) throws Throwable {
        if (checkFlag) {
            //检查页码page是否合法
            if (pageNum > totalPage) {
                throw new Exception("查询失败，查询页码【" + pageNum + "】 大于总页数 【" + totalPage + "】！！");
            }
        }
    }

    /**
     * 功能描述:  回填总条数和总页数
     *
     * @return:
     * @since: 1.0.0
     * @Author:dalong
     * @Date:
     */
    private void setTotalToPageParams(PageParam pageParam, int total, int pageSize) {
        pageParam.setTotal(total);
        //计算总页数
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        pageParam.setTotalPage(totalPage);
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

    @Bean
    public Interceptor getInterceptor() {
        return new MybatisInterceptor();
    }
}
