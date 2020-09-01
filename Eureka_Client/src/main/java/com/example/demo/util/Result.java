package com.example.demo.util;

import com.example.demo.exception.GlobalException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public Result(){
    }

    public Result(int code,String message){
        this.code = code;
        this.message = message;
    }

    public Result(int code,String message,T data){
        this.code = code;
        this.message = message;
        this.data =data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Result(GlobalException e){
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public static Result success(){
        return new Result(HttpStatus.OK.value(),"成功");
    }

    public static Result error(){
        return new Result(HttpStatus.FORBIDDEN.value(),"服务异常");
    }

    public Result success(T data){
        return new Result(HttpStatus.OK.value(),"成功",data);
    }


}
