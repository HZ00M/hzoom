package com.example.demo.sharding;

import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;
import java.util.Date;

public class QuestionRecordRangeShardingAlgorithm implements RangeShardingAlgorithm<Date> {
    /**
     * Sharding.
     *
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValue        sharding value
     * @return sharding results for data sources or tables's names
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {
        return ShardingUtils.quarterRangeSharding(availableTargetNames, shardingValue);
    }
}
