package com.example.core.design.Pipeline;

import java.util.concurrent.TimeUnit;

public interface Pipe<IN, OUT> {
    /**
     * 下一个pipe实例
     *
     * @param nextPipe
     */
    void setNextPipe(Pipe<IN, OUT> nextPipe);

    /**
     * 初始化Pipe对外提供服务
     *
     * @param context
     */
    void init(PipeContext context);

    /**
     * 停止当前pipe对外提供服务
     */
    void shutdown(long timeout, TimeUnit unit);

    /**
     * 对输入元素进行处理，并将处理结果作为下一个Pipe的输入
     */
    void process(IN input) throws InterruptedException;
}
