package com.example.core.sqlgen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.jdbc.SQL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 封装CRUD
 * 在调用Mapper方法时传入相应的实体, 如果字段类型为String且包含%, 将使用like 进行查询, 该操作仅对select和delete操作有效. insert,update则不受此限制, '%'百分号将作为内容被保存进数据库
 */
public class SQLGen<T> {
    public String select(T obj){
        return new SQL(){
            {
                String tableName = obj.getClass().getSimpleName();

                Annotation[] annotations = obj.getClass().getAnnotations();
                for (Annotation annotation :annotations){
                    if(annotation instanceof TableName){
                        tableName = ((TableName) annotation).value();
                    }
                }
                SELECT("*");
                FROM(tableName);
                try {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field:fields){
                        field.setAccessible(true);
                        Object v = field.get(obj);
                        if(v!=null){
                            //进行字段映射处理
                            String fieldName = field.getName();
                            String entityFieldName = field.getName();
                            Annotation[] fieldAnnotions = field.getAnnotations();
                            for (Annotation annotation :fieldAnnotions){
                                if(annotation instanceof TableField){
                                    fieldName = ((TableField) annotation).value();
                                }
                            }
                            if (v instanceof String && ((String)v).contains("%")){
                                WHERE(fieldName+" like '"+v+"'");
                            }else {
                                WHERE(fieldName+"=#{"+entityFieldName+"}");
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.toString();
    }

    public String update(T obj){
        return new SQL(){
            {
                String tableName = obj.getClass().getSimpleName();

                Annotation[] annotations = obj.getClass().getAnnotations();
                for (Annotation annotation :annotations){
                    if(annotation instanceof TableName){
                        tableName = ((TableName) annotation).value();
                    }
                }
                UPDATE(tableName);
                try {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field:fields){
                        field.setAccessible(true);
                        Object v = field.get(obj);
                        if (v!=null){
                            String fieldName = field.getName();
                            SET(fieldName +"=#{"+fieldName+"}");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                WHERE("id = #{id}");
            }
        }.toString();
    }
    public String insert(T obj){
        return new SQL(){
            {
                String tableName = obj.getClass().getSimpleName();

                Annotation[] annotations = obj.getClass().getAnnotations();
                for (Annotation annotation :annotations){
                    if(annotation instanceof TableName){
                        tableName = ((TableName) annotation).value();
                    }
                }
                INSERT_INTO(tableName);
                try {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field:fields){
                        field.setAccessible(true);
                        Object v = field.get(obj);
                        if(v!=null){
                            //进行字段映射处理
                            String fieldName = field.getName();
                            String entityFieldName = field.getName();
                            Annotation[] fieldAnnotions = field.getAnnotations();
                            for (Annotation annotation :fieldAnnotions){
                                if(annotation instanceof TableField){
                                    fieldName = ((TableField) annotation).value();
                                }
                            }
                            VALUES(fieldName,"#{"+entityFieldName+"}");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.toString();
    }

    public String delete(T obj){
        return  new SQL(){
            {
                String tableName = obj.getClass().getSimpleName();

                Annotation[] annotations = obj.getClass().getAnnotations();
                for (Annotation annotation :annotations){
                    if(annotation instanceof TableName){
                        tableName = ((TableName) annotation).value();
                    }
                }

                DELETE_FROM(tableName);
                try {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field:fields){
                        field.setAccessible(true);
                        Object v = field.get(obj);
                        if(v!=null){
                            //进行字段映射处理
                            String fieldName = field.getName();
                            String entityFieldName = field.getName();
                            Annotation[] fieldAnnotions = field.getAnnotations();
                            for (Annotation annotation :fieldAnnotions){
                                if(annotation instanceof TableField){
                                    fieldName = ((TableField) annotation).value();
                                }
                            }
                            if(v instanceof String &&((String)v).contains("%")){
                                WHERE(fieldName +" like '"+v+"'");
                            }else {
                                WHERE(fieldName+"=#{"+entityFieldName+"}");
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.toString();
    }
}

