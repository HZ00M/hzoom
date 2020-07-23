package com.example.demo.activiti.invoker;

import org.activiti.engine.impl.agenda.AbstractOperation;
import org.activiti.engine.impl.interceptor.DebugCommandInvoker;
import org.activiti.engine.logging.LogMDC;

/**
 * activiti 日志拦截器
 */
public class MDCCommandInvoker extends DebugCommandInvoker {
    @Override
    public void executeOperation(Runnable runnable) {
        boolean mdcEnabled = LogMDC.isMDCEnabled();//取原来的值是否是生效的
        LogMDC.setMDCEnabled(true);
        if (runnable instanceof AbstractOperation) {
            AbstractOperation operation = (AbstractOperation)runnable;
            if (operation.getExecution() != null) {
                /*logger.info("Execution tree while executing operation {} :", operation.getClass());
                logger.info("{}", System.lineSeparator() + ExecutionTreeUtil.buildExecutionTree(operation.getExecution()));*/
                LogMDC.putMDCExecution(operation.getExecution());//记录数据
            }
        }

        super.executeOperation(runnable);
        LogMDC.clear();//【清理MDC信息】为保证环境的清洁
        if(!mdcEnabled){//如果原来的值是不生效的
            //把他的值重新还原一下
            LogMDC.setMDCEnabled(false);

        }
    }
}
