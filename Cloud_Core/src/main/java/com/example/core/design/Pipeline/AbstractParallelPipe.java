package com.example.core.design.Pipeline;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 支持并行处理的Pipe实现类
 * 根据输入元素生成一组子任务，并以并行的方式执行子任务
 */
public abstract class AbstractParallelPipe<IN,OUT,V> extends AbstractPipe<IN,OUT>{
    private final ExecutorService executorService;

    public AbstractParallelPipe(BlockingQueue<IN> queue, ExecutorService executorService){
        super();
        this.executorService = executorService;
    }

    protected abstract List<Callable<V>> buildTask(IN input)throws Exception;

    protected List<Future<V>> invokeParallel(List<Callable<V>> tasks)throws Exception {
        return executorService.invokeAll(tasks);
    }

    protected abstract OUT combineResults(List<Future<V>> subTaskResults)throws Exception;

    @Override
    public OUT doProcess(IN input) throws PipeException {
        OUT out = null;
        try{
            out = combineResults(invokeParallel(buildTask(input)));
        }catch (Exception e){
            throw new PipeException(this,input,"Task Failed ",e);
        }
        return out;
    }
}
