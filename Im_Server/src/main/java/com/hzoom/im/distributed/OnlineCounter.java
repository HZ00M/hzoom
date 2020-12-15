package com.hzoom.im.distributed;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.properties.ConstantsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OnlineCounter implements SmartInitializingSingleton {
    @Autowired
    private CuratorFramework client;
    @Autowired
    private ConstantsProperties constantsProperties;

    private DistributedAtomicLong distributedAtomicLong;

    private Long curValue;

    @Override
    public void afterSingletonsInstantiated() {
        distributedAtomicLong = new DistributedAtomicLong(client, constantsProperties.getNodesPath(), new RetryNTimes(10, 30));
    }

    public boolean increment() {
        boolean result = false;
        try {
            AtomicValue<Long> val = distributedAtomicLong.increment();
            result = val.succeeded();
            curValue = val.postValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public boolean decrement() {
        boolean result = false;
        try {
            AtomicValue<Long> val = distributedAtomicLong.decrement();
            result = val.succeeded();
            curValue = val.postValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Long getCurValue(){
        return curValue;
    }
}
