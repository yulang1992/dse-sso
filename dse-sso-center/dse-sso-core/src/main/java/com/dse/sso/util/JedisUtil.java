package com.dse.sso.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * redis 缓存类
 * Redis client base on jedis
 * add 2018-12-20
 * @author
 */
public class JedisUtil {
    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    private static final int DEFAULT_TIME =60 * 60 * 24 ;

    /**
     * redis address, like "{ip}"、"{ip}:{port}"、"{redis/rediss}://dse-sso:{password}@{ip}:{port:6379}/{db}"；Multiple "," separated
     */
    private static String address;

    private static String password;

    public static void init(String address,String password) {
        JedisUtil.address = address;
        if(StringUtils.isNotBlank(password)){
            JedisUtil.password=password;
        }
        getInstance();
    }

    // ------------------------ ShardedJedisPool ------------------------
    /**
     *  方式01: Redis单节点 + Jedis单例 : Redis单节点压力过重, Jedis单例存在并发瓶颈 》》不可用于线上
     *      new Jedis("127.0.0.1", 6379).get("cache_key");
     *  方式02: Redis单节点 + JedisPool单节点连接池 》》 Redis单节点压力过重，负载和容灾比较差
     *      new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379, 10000).getResource().get("cache_key");
     *  方式03: Redis分片(通过client端集群,一致性哈希方式实现) + Jedis多节点连接池 》》Redis集群,负载和容灾较好, ShardedJedisPool一致性哈希分片,读写均匀，动态扩充
     *      new ShardedJedisPool(new JedisPoolConfig(), new LinkedList<JedisShardInfo>());
     *  方式03: Redis集群；
     *      new JedisCluster(jedisClusterNodes);
     */

    private static ShardedJedisPool shardedJedisPool;
    private static ReentrantLock INSTANCE_INIT_LOCL = new ReentrantLock(false);

    /**
     * 获取ShardedJedis实例
     *
     * @return
     */
    private static ShardedJedis getInstance() {
        if (shardedJedisPool == null) {
            try {
                if (INSTANCE_INIT_LOCL.tryLock(2, TimeUnit.SECONDS)) {
                    try {
                        if (shardedJedisPool == null) {
                            JedisPoolConfig config = new JedisPoolConfig();
                            config.setMaxTotal(200);
                            config.setMaxIdle(50);
                            config.setMinIdle(8);
                            config.setMaxWaitMillis(10000);         // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
                            config.setTestOnBorrow(true);           // 在获取连接的时候检查有效性, 默认false
                            config.setTestOnReturn(false);          // 调用returnObject方法时，是否进行有效检查
                            config.setTestWhileIdle(true);          // Idle时进行连接扫描
                            config.setTimeBetweenEvictionRunsMillis(30000);     // 表示idle object evitor两次扫描之间要sleep的毫秒数
                            config.setNumTestsPerEvictionRun(10);               // 表示idle object evitor每次扫描的最多的对象数
                            config.setMinEvictableIdleTimeMillis(60000);        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义

                            // JedisShardInfo List
                            List<JedisShardInfo> jedisShardInfos = new LinkedList<JedisShardInfo>();

                            String[] addressArr = address.split(",");
                            for (int i = 0; i < addressArr.length; i++) {
                                //addressArr[i] 127.0.0.1:6379
                                JedisShardInfo jedisShardInfo = new JedisShardInfo(addressArr[i]);
                                jedisShardInfo.setPassword(password);
                                jedisShardInfos.add(jedisShardInfo);
                            }
                            shardedJedisPool = new ShardedJedisPool(config, jedisShardInfos);
                            logger.info(">>>>>>>>>>> shardedJedisPool, JedisUtil.ShardedJedisPool init success.");
                        }

                    } finally {
                        INSTANCE_INIT_LOCL.unlock();
                    }
                }

            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (shardedJedisPool == null) {
            throw new NullPointerException(">>>>>>>>>>>shardedJedisPool, JedisUtil.ShardedJedisPool is null.");
        }

        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        return shardedJedis;
    }

    public static void close() throws IOException {
        if(shardedJedisPool != null) {
            shardedJedisPool.close();
        }
    }


    // ------------------------ serialize and unserialize ------------------------

    /**
     * 将对象-->json 字符串 (由于jedis中不支持直接存储object所以转换成 字符串)
     *
     * @param object
     * @return
     */
    private static String toJsonString(Object object) {
        if (object != null) {
           return JSON.toJSONString(object);
        }
        return null;
    }

    /**
     * 将 json 串转成object
     *
     * @param
     * @return
     */
    private static Object jsonToObj(String json,Class classz) {
      if(StringUtils.isNotBlank(json)){
        return classz==null ? JSON.toJSON(json) : JSON.parseObject(json,classz);
      }
      return null;
    }

    // ------------------------ jedis util ------------------------
    /**
     * 存储简单的字符串或者是Object 因为jedis没有分装直接存储Object的方法，所以在存储对象需斟酌下
     * 存储对象的字段是不是非常多而且是不是每个字段都用到，如果是的话那建议直接存储对象，
     * 否则建议用集合的方式存储，因为redis可以针对集合进行日常的操作很方便而且还可以节省空间
     */

    /**
     * Set String
     *
     * @param key
     * @param value
     * @param seconds 存活时间,单位/秒
     * @return
     */
    public static String setStringValue(String key, String value, int seconds) {
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key, seconds <= 0 ? DEFAULT_TIME :seconds, value);
        } catch (Exception e) {
            logger.error("更新redis数据异常："+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    /**
     * Set Object
     *
     * @param key
     * @param obj
     * @param seconds 存活时间,单位/秒
     */
    public static String setObjectValue(String key, Object obj, int seconds) {
        String result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.setex(key, seconds <=0?DEFAULT_TIME :seconds,toJsonString(obj));
        } catch (Exception e) {
            logger.error("更新redis数据object异常:"+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    /**
     * Get String
     *
     * @param key
     * @return
     */
    public static String getStringValue(String key) {
        String value = null;
        ShardedJedis client = getInstance();
        try {
            value = client.get(key);
        } catch (Exception e) {
            logger.error("获取数据失败："+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return value;
    }

    /**
     * Get Object
     *
     * @param key
     * @return
     */
    public static Object getObjectValue(String key,Class classz) {
        Object obj = null;
        ShardedJedis client = getInstance();
        try {
            String jsonVal = client.get(key);
            if (StringUtils.isNotBlank(jsonVal)) {
                obj =jsonToObj(jsonVal,classz);
            }
        } catch (Exception e) {
            logger.error("获取对象信息失败："+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return obj;
    }

    /**
     * Delete key
     *
     * @param key
     * @return Integer reply, specifically:
     * an integer greater than 0 if one or more keys were removed
     * 0 if none of the specified key existed
     */
    public static Long del(String key) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.del(key);
        } catch (Exception e) {
            logger.error("del key is fail :"+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    /**
     * incrBy i(+i)
     *
     * @param key
     * @param i
     * @return new value after incr
     */
    public static Long incrBy(String key, int i) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.incrBy(key, i);
        } catch (Exception e) {
            logger.error("按照步长递增失败："+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    /**
     * exists valid
     *
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public static boolean exists(String key) {
        Boolean result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.exists(key);
        } catch (Exception e) {
            logger.error("check key exists fial :"+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    /**
     * expire reset
     *
     * @param key
     * @param seconds 存活时间,单位/秒
     * @return Integer reply, specifically:
     * 1: the timeout was set.
     * 0: the timeout was not set since the key already has an associated timeout (versions lt 2.1.3), or the key does not exist.
     */
    public static long expire(String key, int seconds) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expire(key, seconds);
        } catch (Exception e) {
            logger.error("set key exprise time fail :"+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    /**
     * expire at unixTime
     *
     * @param key
     * @param unixTime
     * @return
     */
    public static long expireAt(String key, long unixTime) {
        Long result = null;
        ShardedJedis client = getInstance();
        try {
            result = client.expireAt(key, unixTime);
        } catch (Exception e) {
            logger.error("expireAt key fail:"+e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public static void main(String[] args) {
     //   String xxlSsoRedisAddress = "redis://yulang:admin@10.100.50.91:6379/0";
        String  xxlSsoRedisAddress = "redis:10.100.50.91:6379";
        init(xxlSsoRedisAddress,"admin");

        setObjectValue("key-999", "6661", 2*60*60);
       // System.out.println(getObjectValue("key-999"));

    }

}
