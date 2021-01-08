package com.hzoom.game.http.common;

import com.hzoom.common.exception.ErrorException;
import com.hzoom.common.error.IError;
import lombok.Data;

@Data
public abstract class AbstractRequestParam {
    protected IError error;

    public void checkParam() {
        haveError();
        if (error != null) {
            throw ErrorException.newBuilder(error).message("异常类 {}", this.getClass().getName()).build();
        }
    }

    protected abstract void haveError();
}
