package com.example.core.design.twoPhaseTermination;

/**
 * 模式角色
 * anstractTerminatableThread
 */
public abstract class AbstractTerminatableThread extends Thread implements Terminatable{

    /**
     * 模式角色
     * terminationToken
     */
    public final TerminationToken terminationToken;

    public AbstractTerminatableThread(){
        this(new TerminationToken());
    }
    public AbstractTerminatableThread(TerminationToken terminationToken){
        super();
        this.terminationToken =terminationToken;
        terminationToken.register(this);
    }

    /**
     * 留给子类，处理的逻辑
     */
    protected abstract void doRun()throws Exception;

    /**
     * 留给子类实现，用于执行清理操作
     */
    protected  void doCleanUp(Exception cause){};

    /**
     * 留给子类实现，执行停止线程所需操作
     */
    protected  void doTerminate(){};


    @Override
    public void run() {
        Exception e = null;
        try {
            for (;;){
                //在执行线程前判断线程停止的标志
                if (terminationToken.isToShutdown()&&terminationToken.reservation.get()<=0){
                    break;
                }
                doRun();
            }
        }catch (Exception ex){
            //希望线程能够响应interrupt调用而退出
            e = ex;
        }finally {
            try {
                doCleanUp(e);
            }finally {
                terminationToken.notifyThreadTermination(this);
            }
        }
    }

    @Override
    public void interrupt(){
        terminate();
    }

    /**
     * 请求停止线程
     */
    @Override
    public void terminate() {
        terminationToken.setToShutdown(true);
        try {
            doTerminate();
        }finally {
            //若无待处理的任务，则试图强制终止线程
            if (terminationToken.reservation.get()<=0){
                super.interrupt();
            }
        }
    }

    public void terminate(boolean waitUtilThreadTerminated){
        terminate();
        if (waitUtilThreadTerminated){
            try {
                this.join();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
