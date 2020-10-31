package com.example.core.concurrent.immutable;

/**
 * 对接类
 */
public class MMSCAgent implements Runnable {
    public void run() {
        boolean isMotify = false;
        String updateTableName = null;
        while (true) {
            /**
             * 省略代码
             * 从连接的socket中读取消息并解析
             * 解析到数据表更新消息后，重置MMSCRouter实例
             */
            if (isMotify) {
                if ("MMSCINFO".equals(updateTableName)) {
                    MMSCRouter.setInstance(new MMSCRouter());
                }
            }
        }
    }
}
