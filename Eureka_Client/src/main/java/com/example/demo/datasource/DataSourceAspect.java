package com.example.demo.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多数据源切面
 *
 */
@Aspect
@Order(1)
@Slf4j
@Component
public class DataSourceAspect {
    @Pointcut("@annotation(com.example.demo.datasource.DataSource)")
    public void dsPointCut()
    {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        MethodSignature signature = (MethodSignature) point.getSignature();

        Method method = signature.getMethod();

        String fullClassName = signature.getDeclaringTypeName();
        if (fullClassName.contains("mapper1")){
            DynamicDataSourceContextHolder.putDataSource(DynamicDataSourceGlobal.WRITE);
        }else if (fullClassName.contains("mapper2")){
            DynamicDataSourceContextHolder.putDataSource(DynamicDataSourceGlobal.CLOUD1);
        }

        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource!=null) {
            DynamicDataSourceContextHolder.putDataSource(dataSource.value());
        }

        try
        {
            return point.proceed();
        }
        finally
        {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDataSource();
        }
    }
}
