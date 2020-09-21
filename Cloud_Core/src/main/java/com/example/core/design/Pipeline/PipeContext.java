package com.example.core.design.Pipeline;

public interface PipeContext {
    /**
     * 对抛出的异常进行处理
     * @param exp
     */
    void handlerError(PipeException exp);
}
