package com.hzoom.demo.sharding;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.aware.ShardingRuleAware;

import java.util.Collection;
import java.util.Date;

public class DatabaseShardingAlgorithm implements ShardingRuleAware, PreciseShardingAlgorithm<Date> {
    private ShardingRule shardingRule;

    @Override
    public void setShardingRule(ShardingRule shardingRule) {
        this.shardingRule = shardingRule;
    }

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        String actualNode = ShardingUtils.quarterPreciseSharding(availableTargetNames, shardingValue);
        int quarter = (int) shardingValue.getValue().getTime() % 2;
        if (quarter==0){
            return "cloud0";
        }else {
            return "cloud1";
        }
    }
}
