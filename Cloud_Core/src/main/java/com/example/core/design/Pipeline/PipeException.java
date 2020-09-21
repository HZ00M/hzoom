package com.example.core.design.Pipeline;

public class PipeException extends Exception {
    /**
     * 抛出异常的pipe实例
     */
    public final Pipe<?, ?> sourcePipe;

    /**
     * 抛出异常的Pipe实例在抛出异常时所处理的元素
     */
    public final Object input;

    public PipeException(Pipe<?, ?> sourcePipe, Object input, String message) {
        super(message);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }

    public PipeException(Pipe<?, ?> sourcePipe, Object input, String message, Throwable cause) {
        super(message, cause);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }
}
