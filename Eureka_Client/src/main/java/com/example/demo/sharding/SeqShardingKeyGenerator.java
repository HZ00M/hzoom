package com.example.demo.sharding;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.UUID;

/**
 * 雪花算法 41bit时间戳+10bit机器id+12bit序列号
 *
 * 由于扩展ShardingKeyGenerator是通过JDK的serviceloader的SPI机制实现的，
 * 因此还需要在resources/META-INF/services目录下配置
 * org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator文件。
 *
 * SPI的英文名称是Service Provider Interface，是Java 内置的服务发现机制。
 * 在开发过程中，将问题进抽象成API，可以为API提供各种实现。
 * 如果现在需要对API提供一种新的实现，我们可以不用修改原来的代码，直接生成新的Jar包，
 * 在包里提供API的新实现。通过Java的SPI机制，可以实现了框架的动态扩展，让第三方的实现能像插件一样嵌入到系统中。
 */
public class SeqShardingKeyGenerator implements ShardingKeyGenerator {
    /** 开始时间截 (2015-01-01) */
    private final long twepoch = 1489111610226L;

    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;

    /** 数据标识id所占的位数 */
    private final long dataCenterIdBits = 5L;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大数据标识id，结果是31 */
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据标识id向左移17位(12+5) */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 工作机器ID(0~31) */
    private long workerId;

    /** 数据中心ID(0~31) */
    private long dataCenterId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    private static SeqShardingKeyGenerator idWorker;

    static {
        idWorker = new SeqShardingKeyGenerator();
    }

    Properties properties = new Properties();

    /**
     * 构造函数
     */
    public SeqShardingKeyGenerator() {

        this.workerId = getWorkId();
        this.dataCenterId = getDataCenterId();

        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }

    }

    @Override
    public Comparable<?> generateKey() {
        // 获取分布式id逻辑
        return SeqShardingKeyGenerator.generateId();
    }

    @Override
    public String getType() {
        return "SEQ";
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }



    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized long nextId(boolean ifEvenNum) {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }


        if(ifEvenNum){
            if (timestamp == lastTimestamp) {
                //相同毫秒内，序列号自增
                sequence = (sequence + 1) & sequenceMask;
                //同一毫秒的序列数已经达到最大
                if (sequence == 0L) {
                    timestamp = timeGen();
                }
            } else {
                //不同毫秒内，序列号置为0
                sequence = 0L;
            }
        }else {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    private static Long getWorkId(){
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for(int b : ints){
                sums += b;
            }
            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0,31);
        }
    }

    private static Long getDataCenterId(){
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i: ints) {
            sums += i;
        }
        return (long)(sums % 32);
    }


    /**
     * 静态工具类
     *
     * @return
     */
    public static synchronized Long generateId(){
        long id = idWorker.nextId(false);
        return id;
    }
}
