package com.example.demo.sharding;

import lombok.experimental.UtilityClass;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

@UtilityClass //构造函数为私有 ,方法为静态方法
public class ShardingUtils {
    public static final String QUARTER_SHARDING_PATTERN = "%s_%d_q%d";

    /**
     * logicTableName_{year}_q{quarter}
     * 按季度范围分片
     * @param availableTargetNames 可用的真实表集合
     * @param shardingValue 分片值
     * @return
     */
    public Collection<String> quarterRangeSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {
        // 这里就是根据范围查询条件，筛选出匹配的真实表集合
        return availableTargetNames;
    }

    /**
     * logicTableName_{year}_q{quarter}
     * 按季度精确分片
     * @param availableTargetNames 可用的真实表集合
     * @param shardingValue 分片值
     * @return
     */
    public static String quarterPreciseSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        // 这里就是根据等值查询条件，计算出匹配的真实表
        Random random = new Random();
        Calendar c = Calendar.getInstance();
        c.setTime(shardingValue.getValue());
        String actualNode = String.format(QUARTER_SHARDING_PATTERN,shardingValue.getLogicTableName(),c.get(Calendar.YEAR),shardingValue.getValue().getTime()%2);
        return actualNode;
    }
}
