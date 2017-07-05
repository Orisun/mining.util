package com.orisun.mining.util.dao.redis;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orisun.mining.util.Path;

public class TestRedisLock {

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
		final String lockname = "JHHH";
		RedisLock.getInstance(RedisDBName.POSITION_REC).releaseLock(lockname);
		if (RedisLock.getInstance(RedisDBName.POSITION_REC).getLock(lockname)) {
			Assert.assertTrue(true);// 应该获得锁
		} else {
			Assert.assertTrue(false);
		}
		if (RedisLock.getInstance(RedisDBName.POSITION_REC).getLock(lockname)) {
			Assert.assertTrue(false);
		} else {
			Assert.assertTrue(true);// 不应该获得锁
		}
		RedisLock.getInstance(RedisDBName.POSITION_REC).releaseLock(lockname);// 释放锁
		if (RedisLock.getInstance(RedisDBName.POSITION_REC).getLock(lockname)) {
			Assert.assertTrue(true);// 应该获得锁
		} else {
			Assert.assertTrue(false);
		}
	}
}
