package com.example.demo.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;

@Slf4j
public class ProcessEventListener implements ActivitiEventListener {
    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        ActivitiEventType eventType = activitiEvent.getType();
        log.info("eventType : {} ProcessInstanceId {}" ,eventType, activitiEvent.getProcessInstanceId());
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
