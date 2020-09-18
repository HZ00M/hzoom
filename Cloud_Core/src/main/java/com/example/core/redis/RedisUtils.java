package com.example.core.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

@Slf4j
@Component
public class RedisUtils {
    /**
     * 查看键的剩余时间  返回三种值 -2(健已经不存在了)  -1(表示永久有效)  大于0的数(还有多少有效时间)
     */
    public static final long NOT_EXIST = -2;
    public static final long FOREVER_EXIST = -1;
    SerializerFeature[] features = new SerializerFeature[]{
            SerializerFeature.WriteClassName,
    };
    /**
     * 数据源
     */
    @Autowired
    private JedisPool jedisPool;

    /**
     * 退出然后关闭Jedis连接。
     */
    @SuppressWarnings("all")
    public static void closeJedis(Jedis jedis) {
        if (jedis.isConnected()) {
            try {
                try {
                    jedis.quit();
                } catch (Exception e) {
                }
                jedis.disconnect();
            } catch (Exception e) {

            }
        }
    }


    /******************  下面开始是模板方法！ 上面定义公共函数  ***************************************************/

    /**
     * 获取自增id，正整数long 用于jpushsendNo.
     * 改用redis自身DB支持，不访问数据库
     *
     * @param name 业务id， 目前可以使用"jpush","chat_room"
     * @return long
     */
    public synchronized long getUniqNoSequency(String name) {
        return incr(name + "sequency");
    }

    /**
     * 用户未读信息计数（消息、分享。。一切推送的消息）
     *
     * @param userId
     * @param reset  清零标记， ture-归零;  默认false
     * @return 0--n
     */
    public long getMessageCounter(long userId, boolean reset) {
        String key = "unreadMessage-" + userId;
        //获取缓存里面的数据
        if (reset) {
            del(key);
            return 0;
        } else {
            Long incr = incr(key);
            return incr == null ? 0 : incr;
        }
    }

    /**
     * 是否在CD 冷却时间 的时间里, 控制调用间隔
     * 线程不安全的,注意并发,在外面加锁
     *
     * @param key   CD 的key
     * @param cdSec CD的时间
     * @return
     */
    public boolean isCD(String key, int cdSec) {
        boolean isCD = exists(key);
        if (!isCD) {
            setex(key, cdSec, "CD");
        }
        return isCD;
    }

    /**
     * 是否在CD 冷却时间 的时间里, 控制调用间隔
     * 加入线程安全,用 lockObj 参数的对象做锁对象
     *
     * @param key     CD 的key
     * @param cdSec   CD的时间
     * @param lockObj 线程锁对象
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public boolean isCD(String key, int cdSec, Object lockObj) {
        synchronized (lockObj) {
            boolean isCD = exists(key);
            if (!isCD) {
                setex(key, cdSec, "CD");
            }
            return isCD;
        }
    }

    /**
     * 执行有返回结果的action。
     */
    protected <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            return jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            //logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * 执行无返回结果的action。
     */
    protected void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            log.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    // ////////////// 常用方法的封装 ///////////////////////// //

    // ////////////// 公共 ///////////////////////////

    /**
     * 根据连接是否已中断的标志，分别调用returnBrokenResource或returnResource。
     */
    protected void closeResource(Jedis jedis, boolean connectionBroken) {
        if (jedis != null) {
            try {
                if (connectionBroken) {
                    jedisPool.returnBrokenResource(jedis);
                } else {
                    jedisPool.returnResource(jedis);
                }
            } catch (Exception e) {
                //logger.error("Error happen when return jedis to pool, try to close it directly.", e);
                closeJedis(jedis);
            }
        }
    }

    /**
     * 获取内部的pool做进一步的动作。
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    // ////////////// 关于String ///////////////////////////

    /**
     * 删除key, 如果key存在返回true, 否则返回false。
     */
    @SuppressWarnings("all")
    public boolean del(final String key) {
        return execute(jedis -> jedis.del(key) == 1 ? true : false);
    }

    public void flushDB() {
        execute((JedisActionNoResult) jedis -> jedis.flushDB());
    }

    /**
     * 如果key不存在, 返回null.
     */
    public String get(final String key) {
        return execute((JedisAction<String>) jedis -> jedis.get(key));
    }

    public List<String> mget(final String... keys) {
        return execute((JedisAction<List<String>>) jedis -> jedis.mget(keys));
    }

    public byte[] get(final byte[] key) {
        return execute((JedisAction<byte[]>) jedis -> jedis.get(key));
    }

    /**
     * 获得 Object 使用 ProtoStuffSerializer 序列化对象超快 ,
     * 注意只能序列化 对象类型 , 不能序列化map 之类的 使用时注意之类的坑,做好单元测试
     */
    @SuppressWarnings("all")
    public <T> T getObject(String key, Class<T> cls) {
        String json = get(key);
        try {
            return json == null ? null : JSON.parseObject(json, cls);
        } catch (JSONException e) {
            log.error("Redis getObjectError key:" + key + "json{}" + json + "class:" + cls, e);
            return null;
        }
    }


    public <T> List<T> mgetObjectList(Class<T> cls, String... keys) {
        List<String> jsons = mget(keys);
        List<T> ts = new ArrayList<>();
        jsons.stream().forEach(s -> {
            T t = JSON.parseObject(s, cls);
            if (null != t) {
                ts.add(t);
            }
        });
        return ts;
    }


    /**
     * 获得 List<T>  使用 ProtoStuffSerializer 序列化对象超快 ,
     * 注意只能序列化 对象类型 , 不能序列化map 之类的 使用时注意之类的坑,做好单元测试
     */
    public <T> List<T> getObjectList(String key, Class<T> cls) {
        String json = get(key);
        return json == null ? null : JSON.parseArray(json, cls);
    }

    /**
     * 如果key不存在, 返回0.
     */
    public Long getAsLong(final String key) {
        String result = get(key);
        return result != null ? Long.valueOf(result) : 0;
    }

    /**
     * 如果key不存在, 返回0.
     */
    public Integer getAsInt(final String key) {
        String result = get(key);
        return result != null ? Integer.valueOf(result) : 0;
    }

    public void set(final String key, final String value) {
        execute((JedisActionNoResult) jedis -> jedis.set(key, value));
    }

    public void set(final byte[] key, final byte[] value) {
        execute((JedisActionNoResult) jedis -> jedis.set(key, value));
    }

    /**
     * 设置 Object 使用 ProtoStuffSerializer 序列化对象超快 ,
     * 注意只能序列化 对象类型 , 不能序列化map 之类的 使用时注意之类的坑,做好单元测试
     */
    public void setexObject(final String key, final int seconds, Object object) {
        if (object != null) {
            setex(key, seconds, JSON.toJSONString(object, features));
        }
    }

    /**
     * 设置 Object 使用 ProtoStuffSerializer 序列化对象超快 ,
     * 注意只能序列化 对象类型 , 不能序列化map 之类的 使用时注意之类的坑,做好单元测试
     */
    public void setObject(final String key, Object object) {
        set(key, JSON.toJSONString(object, features));
    }

    public void setex(final String key, final int seconds, final String value) {
        execute((JedisActionNoResult) jedis -> jedis.setex(key, seconds > 0 ? seconds : 1, value));
    }

    public void setex(final byte[] key, final int seconds, final byte[] value) {
        execute((JedisActionNoResult) jedis -> jedis.setex(key, seconds, value));
    }

    /**
     * 如果key还不存在则进行设置，返回true，否则返回false.
     */
    public boolean setnx(final String key, final String value) {
        return execute(jedis -> jedis.setnx(key, value) == 1);
    }

    /**
     * 将 key 中储存的数字值增一。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     *
     * @return 执行 INCR 命令之后 key 的值。
     */
    public Long incr(final String key) {
        return execute((JedisAction<Long>) jedis -> jedis.incr(key));
    }

    /**
     * 将 key 中储存的数字值增加integer。
     * * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。
     *
     * @param key
     * @param integer
     * @return
     */
    public Long incrBy(final String key, final long integer) {
        return execute((JedisAction<Long>) jedis -> jedis.incrBy(key, integer));
    }

    // ////////////// 关于List ///////////////////////////

    /**
     * 将 key 中储存的数字值减一。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
     *
     * @return 执行 DECR 命令之后 key 的值。
     */
    public Long decr(final String key) {
        return execute((JedisAction<Long>) jedis -> jedis.decr(key));
    }

    /**
     * 将 key 中储存的数字值减少integer。
     * * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 decrBy 操作。
     *
     * @param key
     * @param integer
     * @return
     */
    public Long decrBy(final String key, final long integer) {
        return execute((JedisAction<Long>) jedis -> jedis.decrBy(key, integer));
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     *
     * @param key
     * @param values
     */
    public void lpush(final String key, final String... values) {
        execute((JedisAction<Object>) jedis -> jedis.lpush(key, values));
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾
     */
    public void rpush(final String key, final String value) {
        execute((JedisAction<Object>) jedis -> jedis.rpush(key, value));
    }

    /**
     * 获得list下标值
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     */
    public String lindex(final String key, final long index) {
        return execute((JedisAction<String>) jedis -> jedis.lindex(key, index));
    }

    /**
     * 移除并返回列表 key 的头元素。
     */
    public String lpop(final String key) {
        return execute((JedisAction<String>) jedis -> jedis.lpop(key));
    }

    /**
     * Return the length of the list stored at the specified key. If the key
     * does not exist zero is returned (the same behaviour as for empty lists).
     * If the value stored at key is not a list an error is returned.
     */
    public long llen(final String key) {
        return execute((JedisAction<Long>) jedis -> jedis.llen(key));
    }

    /**
     * 删除List中的第一个等于value的元素，value不存在或key不存在时返回0.
     */
    public boolean lremOne(final String key, final String value) {
        return execute(jedis -> {
            Long count = jedis.lrem(key, 1, value);
            return (count == 1);
        });
    }

    /**
     * Return the specified elements of the list stored at the specified key.
     * Start and end are zero-based indexes. 0 is the first element of the list
     * (the list head), 1 the next element and so on.
     */
    public List<String> lrange(final String key, final long start, final long end) {
        return execute((JedisAction<List<String>>) jedis -> jedis.lrange(key, start, end));

    }

    /**
     * 删除List中的所有等于value的元素，value不存在或key不存在时返回0.
     */
    public boolean lremAll(final String key, final String value) {
        return execute(jedis -> {
            Long count = jedis.lrem(key, 0, value);
            return (count > 0);
        });
    }

    // ////////////// 关于Sorted Set ///////////////////////////


    /**
     * 加入Sorted set, 如果member在Set里已存在，只更新score并返回false,否则返回true.
     */
    public boolean zadd(final String key, final String member, final double score) {
        return execute(jedis -> jedis.zadd(key, score, member) == 1);
    }

    /**
     * 对有序集合中指定成员的分数加上增量 score
     */
    public boolean zincrby(final String key, final String member, final long score) {
        return execute(jedis -> jedis.zincrby(key, score, member) == 1);
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列
     */
    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        return execute((JedisAction<Set<Tuple>>) jedis -> jedis.zrevrangeWithScores(key, start, end));
    }

    /**
     * 获取member 的 score
     */
    public Double zscore(final String key, final String member) {
        return execute(jedis -> {
            Double score = jedis.zscore(key, member);
            return score == null ? 0D : score;
        });
    }

    /**
     * 返回有序集中成员的排名。其中有序集成员按分数值递减(从大到小)排序。
     * 如果为空返回默认最大值10000
     */
    public long zrevrank(final String key, final String member) {
        return execute(jedis -> {
            Long rank = jedis.zrevrank(key, member);
            return rank == null ? 10000 : rank;
        });
    }

    /**
     * 删除sorted set中的元素，成功删除返回true，key或member不存在返回false。
     */
    public boolean zrem(final String key, final String member) {
        return execute(jedis -> jedis.zrem(key, member) == 1);
    }

    /**
     * 删除分数区间的元素
     */
    public boolean zremrangeByScore(final String key, final String beginScore, final String endScore) {
        return execute(jedis -> jedis.zremrangeByScore(key, beginScore, endScore) == 1);
    }

    /**
     * 返回List长度, key不存在时返回0，key类型不是sorted set时抛出异常.
     */
    public long zcard(final String key) {
        return execute((JedisAction<Long>) jedis -> jedis.zcard(key));
    }


    public long ttl(final String key) {
        return execute((JedisAction<Long>) jedis -> jedis.ttl(key));
    }

    //***************hash 操作 ******************/
    public String hmset(final String key, final Map<String, String> map) {
        return execute((JedisAction<String>) jedis -> jedis.hmset(key, map));
    }

    public Long hset(final String key, final String field, final String value) {
        return execute((JedisAction<Long>) jedis -> jedis.hset(key, field, value));
    }

    public String hget(final String key, final String field) {
        return execute((JedisAction<String>) jedis -> jedis.hget(key, field));
    }

    public Map<String, String> hgetAll(final String key) {
        return execute((JedisAction<Map<String, String>>) jedis -> jedis.hgetAll(key));
    }

    public <T> Map<String, T> hgetAll(final String key, final Class<T> cls) {
        Map<String, String> mapString = hgetAll(key);
        Map<String, T> mapT = new HashMap<>();
        mapString.forEach((keyString, value) -> {
            mapT.put(keyString, JSON.parseObject(value, cls));
        });
        return mapT;
    }

    public <T> List<T> hmget(final String key, final Class<T> cls, String[] keyList) {
        List<String> stringList = hmget(key, keyList);
        List<T> listT = new ArrayList<>();
        stringList.forEach(value -> {
            listT.add(JSON.parseObject(value, cls));
        });
        return listT;
    }


    public List<String> hmget(final String key, String[] keyList) {
        return execute((JedisAction<List<String>>) jedis -> jedis.hmget(key, keyList));
    }

    public Long hset(final String key, final String field, final Object object) {
        return hset(key, field, JSON.toJSONString(object, features));
    }

    public Long hset(final String key, final String field, final Object object, final int expireSecond) {

        Long result = execute((JedisAction<Long>) jedis -> {
            return jedis.hset(key, field, JSON.toJSONString(object, features));
        });

        return result;
    }

    public <T> T hget(final String key, final String field, Class<T> cls) {
        String json = hget(key, field);
        return json == null ? null : JSON.parseObject(json, cls);
    }

    // map 里的值 递增 或减少
    public Long hincrBy(final String key, final String field, final Long num) {
        return execute((JedisAction<Long>) jedis -> jedis.hincrBy(key, field, num));
    }

    public Long hdel(final String key, final String field) {
        return execute((JedisAction<Long>) jedis -> jedis.hdel(key, field));
    }

    public Boolean hexists(final String key, final String field) {
        return execute((JedisAction<Boolean>) jedis -> jedis.hexists(key, field));
    }

    public Long hlen(final String key) {
        return execute((JedisAction<Long>) jedis -> jedis.hlen(key));
    }


    /**
     * 设置一个超时的时间
     */
    public void expire(final String key, final int seconds) {
        execute((JedisActionNoResult) jedis -> jedis.expire(key, seconds));
    }

    public boolean exists(final String key) {
        return execute((JedisAction<Boolean>) jedis -> jedis.exists(key));
    }


    /**
     * 有返回结果的回调接口定义。
     */
    public interface JedisAction<T> {
        T action(Jedis jedis);
    }


    /**
     * 无返回结果的回调接口定义。
     */
    public interface JedisActionNoResult {
        void action(Jedis jedis);
    }
}
