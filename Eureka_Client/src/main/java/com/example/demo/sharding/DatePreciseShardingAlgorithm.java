package com.example.demo.sharding;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 精确分片算法
 */
@Component
public class DatePreciseShardingAlgorithm implements PreciseShardingAlgorithm<Date> {
//    public static final String CREATE_TABLE_SQL = "show create table ";
//    public static ConcurrentMap createSqlMap = new ConcurrentHashMap();
//    @Autowired
//    SqlSessionFactory sqlSessionFactory;

    /**
     * Sharding.
     *
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValue        sharding value
     * @return sharding result for data source or table's name
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        String actualNode = ShardingUtils.quarterPreciseSharding(availableTargetNames, shardingValue);
//        String checkSql = CREATE_TABLE_SQL + shardingValue.getLogicTableName();
//        if (!createSqlMap.containsKey(checkSql)) {
//            SqlSession sqlSession = sqlSessionFactory.openSession();
//            String createSql = (String) sqlSession.selectOne(checkSql);
//            createSql.replace(shardingValue.getLogicTableName(),actualNode);
//            sqlSession.update(createSql);
//            sqlSession.close();
//            createSqlMap.putIfAbsent(checkSql,true);
//        }
        return actualNode;

    }
}
