package com.example.core.design.producerConsumer;


import com.example.core.design.twoPhaseTermination.AbstractTerminatableThread;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 角色模式producer
 */
public class AttachmentProssor {
    private final String DIR = "/home";

    /**
     * 角色模式 channel
     */
    private final Channel<String> channel = new BlockingQueueChannel<String>(new ArrayBlockingQueue<String>(200));

    /**
     * 模式角色Consumer
     */
    private final AbstractTerminatableThread indexingThread = new AbstractTerminatableThread() {
        @Override
        protected void doRun() throws Exception {
            String file = channel.take();
            try {
                indecFile(file);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                terminationToken.reservation.decrementAndGet();
            }
        }
    };

    public void init(){
        indexingThread.start();
    }

    public void shutdown(){
        indexingThread.terminate();
    }

    public void saveAttachment(String file){
        try {
            channel.put(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }indexingThread.terminationToken.reservation.incrementAndGet();
    }

    private void indecFile(String file){};
}
