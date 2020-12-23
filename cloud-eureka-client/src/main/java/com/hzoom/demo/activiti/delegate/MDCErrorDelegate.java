package com.hzoom.demo.activiti.delegate;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

@Slf4j
public class MDCErrorDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("run MDCErrorDelegate");
        throw new RuntimeException("only test!");
    }
}
