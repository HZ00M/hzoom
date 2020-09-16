package com.example.demo.exception;

import com.example.demo.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = GlobalException.class)
    @ResponseBody
    public Result exceptionHandler(GlobalException e) {
        Throwable throwable = getGlobalException(e);
        if (!Objects.isNull(throwable)) {
            return new Result<>(e.getCode(),e.getMessage());
        }
        return Result.error();
    }

    /**
     * 若有异常进行嵌套，打印出每个异常的堆栈信息，若包含自定义异常，返回最内部的GlobalException异常。
     * @param e
     * @return
     */
    private Throwable getGlobalException(Throwable e) {
        if (e == null) {
            return null;
        } else if (e instanceof GlobalException) {
            if(((GlobalException)e).getMessage()!=null) {
                logger.error(((GlobalException) e).getMessage()+((GlobalException) e).getThrowable());
            }
            e.printStackTrace();
            Throwable temp = getGlobalException(e.getCause());
            if (temp == null) {
                return e;
            } else {
                return temp;
            }
        } else {
            e.printStackTrace();
            return getGlobalException(e.getCause());
        }
    }

}
