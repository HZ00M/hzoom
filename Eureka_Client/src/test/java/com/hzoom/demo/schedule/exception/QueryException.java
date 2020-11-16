package com.hzoom.demo.schedule.exception;

import java.util.function.Function;

/**
 *   任务异常封装
 */
public class QueryException extends Exception {
    private final Function function;
    public QueryException(String message, Throwable throwable, Function function){
        super(message,throwable);
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }
}
