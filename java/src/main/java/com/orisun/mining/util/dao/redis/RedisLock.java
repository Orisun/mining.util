package com.orisun.mining.util.dao.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * RedisLock在获得锁时允许设置超时时间，即锁会在3种情况下被释放：<br>
 * <li>显式调用{@link #releaseLock(String)}}
 * <li>达到获得锁时设定的超时时间
 * <li>达到redis服务器设置的失效期
 * 
 * @Author:orisun
 * @Since:2016-3-26
 * @Version:1.0
 */
public class RedisLock {
	private static Log logger = LogFactory.getLog(RedisLock.class);

	private static final int DEFAULT_EXPIRE_TIME = 10;
	private JedisClientPool pool;

	private static Map<RedisDBName, RedisLock> redisMap = new ConcurrentHashMap<RedisDBName, RedisLock>();

	private RedisLock() {
	}

	public static RedisLock getInstance(RedisDBName dbname) {
		if (!redisMap.containsKey(dbname)) {
			RedisLock inst = new RedisLock();
			JedisClientPool jedisPool = RedisPoolManager.getJedisClientPool(dbname);
			inst.setJedis(jedisPool);
			redisMap.put(dbname, inst);
		}
		return redisMap.get(dbname);
	}

	private void setJedis(JedisClientPool jedis) {
		this.pool = jedis;
	}

	/**
	 * 尝试获取分布式锁，如果获取成功，返回true，否则返回false<br>
	 * 没果没有显式释放锁，1小时之后锁自动被释放
	 * 
	 * @param lockName
	 * @return
	 */
	public boolean getLock(String lockName) {
		int live = 60 * 60 * 1;
		return getLock(lockName, live);
	}

	/**
	 * 尝试获取分布式锁，如果获取成功，返回true，否则返回false<br>
	 * 没果没有显式释放锁，{@code live}秒之后锁自动被释放
	 * 
	 * @param lockName
	 * @param live
	 *            锁的存活时间，到期后锁会强制被释放，单位秒。
	 * @return
	 */
	public boolean getLock(String lockName, int live) {
		Jedis jedis = null;
		boolean rect = false;
		try {
			jedis = pool.getResource();
			// 若key不存在，则存储 ，并返回1
			Long i = jedis.setnx(lockName, lockName);
			if (i == 1L) {
				// 设置key的过期时间
				if (live < 0) {
					live = DEFAULT_EXPIRE_TIME;
				}
				jedis.expire(lockName, live);
				logger.info("get redis lock " + lockName + " ,live " + live + " seconds");

				rect = true;
			} else { // 已存在锁
				logger.info("lockName: " + lockName + " locked by other business");
				rect = false;
			}
		} catch (JedisConnectionException je) {
			logger.error(je.getMessage(), je);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (jedis != null) {
					jedis.close();
				}
			} catch (Exception e) {
			}
		}
		return rect;
	}

	/**
	 * 手动释放分布式锁
	 * 
	 * @param lockName
	 */
	public void releaseLock(String lockName) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(lockName);
			logger.info("release lock " + lockName);
		} catch (JedisConnectionException je) {
			logger.error(je.getMessage(), je);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			jedis.close();
		}
	}
}

