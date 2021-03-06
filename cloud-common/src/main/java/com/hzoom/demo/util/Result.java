package com.hzoom.demo.util;

import com.hzoom.demo.exception.GlobalException;
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


    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Result(GlobalException e){
        this.code = e.getCode();
        this.message = e.getMessage();
    }


    public static Result result(ResultCode resultCode){
        return new Result(resultCode.getCode(),resultCode.getMessage());
    }

    public static Result success(){
        return new Result(HttpStatus.OK.value(),"成功");
    }

    public static Result success(String message){
        return new Result(HttpStatus.OK.value(),message);
    }

    public Result success(T data){
        return new Result(HttpStatus.OK.value(),"成功",data);
    }

    public static Result error(){
        return new Result(0,"失败");
    }
    public static Result error(String message){
        return new Result(0,message);
    }
    public Result error(T data){
        return new Result(HttpStatus.OK.value(),"失败",data);
    }

}

