package com.orisun.mining.util.dao.redis;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orisun.mining.util.Path;

import redis.clients.jedis.Jedis;

public class TestSentinel {

	@BeforeClass
	public static void setup() {
		String basePath = Path.getCurrentPath();
		String confPath = basePath + "/config/";
		RedisPoolManager.configRedisPool(RedisDBName.POSITION_REC, confPath + "sentinel_redis.properties");// 哨兵模式
		// RedisPoolManager.configRedisPool(RedisDBName.POSITION_REC, confPath +
		// "redis.properties",null);//主从模式
		PropertyConfigurator.configure(confPath + "log4j.properties");
	}

	@Test
	public void testSentinel() {
		JedisClientPool pool = RedisPoolManager.getJedisClientPool(RedisDBName.POSITION_REC);
		Jedis redisClient = null;
		try {
			redisClient = pool.getResource();
			final String key = "name";
			final String value = "ORISUN";
			redisClient.set(key, value);
			System.out.println(redisClient.get(key));
			Assert.assertEquals(redisClient.get(key), value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (redisClient != null) {
				redisClient.close();
			}
		}
	}
}
