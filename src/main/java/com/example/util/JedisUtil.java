package com.example.util;

import com.example.constant.Constant;
import com.example.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

/**
 * JedisUtil
 */
@Component
public class JedisUtil {

    /**
     * 静态注入JedisPool连接池
     * 现在改为静态注入JedisPool连接池，JedisUtil直接调用静态方法即可
     */
    private static JedisPool jedisPool;

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        JedisUtil.jedisPool = jedisPool;
    }

    /**
     * 获取Jedis实例
     */
    public static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            } else {
                throw new CustomException("获取Jedis资源异常");
            }
        } catch (Exception e) {
            throw new CustomException("获取Jedis资源异常:" + e.getMessage());
        }
    }

    /**
     * 获取redis键值
     */
    public static String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            throw new CustomException("获取Redis键值get方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 批量获取redis键值
     */
    public static List<String> mget(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mget(keys);
        } catch (Exception e) {
            throw new CustomException("获取Redis键值mget方法异常:key=" + keys + " cause=" + e.getMessage());
        }
    }

    /**
     * 设置redis键值
     */
    public static String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception e) {
            throw new CustomException("设置Redis键值set方法异常:key=" + key + " value=" + value + " cause=" + e.getMessage());
        }
    }

    /**
     * 批量设置redis键值
     */
    public static String mset(String... keysvalues) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mset(keysvalues);
        } catch (Exception e) {
            throw new CustomException("设置Redis键值set方法异常:keysvalues=" + keysvalues + " cause=" + e.getMessage());
        }
    }

    /**
     * 设置redis键值
     */
    public static String set(String key, String value, int expire) {
        String result;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.set(key, value);
            if (Constant.OK.equals(result)) {
                jedis.expire(key, expire);
            }
            return result;
        } catch (Exception e) {
            throw new CustomException("设置Redis键值set方法异常:key=" + key + " value=" + value + " cause=" + e.getMessage());
        }
    }

    /**
     * 删除key
     */
    public static Long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key.getBytes());
        } catch (Exception e) {
            throw new CustomException("删除Redis的键delKey方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 键值自增
     */
    public static Long incr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        } catch (Exception e) {
            throw new CustomException("键值自增incr方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 键值自减
     */
    public static Long decr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        } catch (Exception e) {
            throw new CustomException("键值自减decr方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * key是否存在
     */
    public static Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key.getBytes());
        } catch (Exception e) {
            throw new CustomException("查询Redis的键是否存在exists方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 模糊查询获取key集合(keys的速度非常快，但在一个大的数据库中使用它仍然可能造成性能问题，生产不推荐使用)
     */
    public static Set<String> keysS(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key);
        } catch (Exception e) {
            throw new CustomException("模糊查询Redis的键集合keysS方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 模糊查询获取key集合(keys的速度非常快，但在一个大的数据库中使用它仍然可能造成性能问题，生产不推荐使用)
     */
    public static Set<byte[]> keysB(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key.getBytes());
        } catch (Exception e) {
            throw new CustomException("模糊查询Redis的键集合keysB方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 获取过期剩余时间
     */
    public static Long ttl(String key) {
        Long result = -2L;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.ttl(key);
            return result;
        } catch (Exception e) {
            throw new CustomException("获取Redis键过期剩余时间ttl方法异常:key=" + key + " cause=" + e.getMessage());
        }
    }

    /**
     * 脚本执行
     */
    public static Object eval(String script, List<String> keys, List<String> args) {
        Object result = null;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.eval(script, keys, args);
            return result;
        } catch (Exception e) {
            throw new CustomException("Redis脚本执行eval方法异常:script=" + script + " keys=" +
                    keys.toString() + " args=" + args.toString() + " cause=" + e.getMessage());
        }
    }

}
