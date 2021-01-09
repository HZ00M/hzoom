package com.hzoom.game.http.common;

import com.alibaba.fastjson.JSONObject;
import com.hzoom.game.error.GameCenterError;
import com.hzoom.game.error.IError;
import com.hzoom.game.exception.ErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * * @Description: 全局异常捕获
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionCatch {

    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public BaseResponse<JSONObject> exceptionHandler(Throwable throwable) {
        IError error = null;
        if (throwable instanceof ErrorException) {
            ErrorException errorException = (ErrorException) throwable;
            error = errorException.getError();
            log.error("服务器异常,{}", throwable.getMessage());
        } else {
            error = GameCenterError.UNKNOWN;
            log.error("服务器异常,{}", throwable);
        }
        JSONObject data = new JSONObject();//统一给客户端返回结果
        data.put("cause ", throwable.getMessage());
        BaseResponse<JSONObject> response = new BaseResponse<>(error);
        response.setData(data);
        return response;
    }
}
