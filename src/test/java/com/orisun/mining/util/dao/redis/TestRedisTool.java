package com.orisun.mining.util.dao.redis;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orisun.mining.util.DataTransform;
import com.orisun.mining.util.Path;

import redis.clients.jedis.Jedis;

public class TestRedisTool {

	@BeforeClass
	public static void setup() {
		String basePath = Path.getCurrentPath();
		String confPath = basePath + "/config/";
		RedisPoolManager.configRedisPool(RedisDBName.TALENT_REC, confPath + "sentinel_redis.properties");// 哨兵模式
		// RedisPoolManager.configRedisPool(RedisDBName.TALENT_REC, confPath +
		// "redis.properties", null);//主从模式
		PropertyConfigurator.configure(confPath + "log4j.properties");
	}

	@Test
	public void testTraverseLogList() {
		final byte[] key = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0 };
		JedisClientPool pool = RedisPoolManager.getJedisClientPool(RedisDBName.TALENT_REC);
		Jedis redisClient = pool.getResource();
		// 清空key
		redisClient.del(key);
		// 往list中填充元素
		for (int i = 0; i < 1000; i++) {
			redisClient.rpush(key, new byte[] { (byte) 100 });
			// if (i % 100 == 0) {
			// System.out.println("have write " + i + " values");
			// }
		}
		// 分页遍历list
		long begin = System.currentTimeMillis();
		List<byte[]> result1 = RedisTool.traverseLongList(redisClient, key);
		long end = System.currentTimeMillis();
		System.out.println("batch split use time " + (end - begin));
		// 一次性遍历list
		begin = System.currentTimeMillis();
		List<byte[]> result2 = redisClient.lrange(key, 0, -1);
		end = System.currentTimeMillis();
		System.out.println("once use time " + (end - begin));
		Assert.assertEquals(result1.size(), result2.size());
		// 清空key
		redisClient.del(key);
	}

	@Test
	public void testTraverseBigSet() {
		final byte[] key = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 1 };
		JedisClientPool pool = RedisPoolManager.getJedisClientPool(RedisDBName.TALENT_REC);
		Jedis redisClient = pool.getResource();
		// 清空key
		redisClient.del(key);
		Random rnd = new Random();
		// 往set中填充元素
		for (int i = 0; i < 300; i++) {
			redisClient.sadd(key, DataTransform.intToBytes(rnd.nextInt(), false));
			// if (i % 100 == 0) {
			// System.out.println("have write " + i + " values");
			// }
		}
		// 分页遍历set
		long begin = System.currentTimeMillis();
		List<byte[]> result1 = RedisTool.traverseBigSet(redisClient, key);
		long end = System.currentTimeMillis();
		System.out.println("batch split use time " + (end - begin));

		// 一次性遍历Set
		begin = System.currentTimeMillis();
		Set<byte[]> result2 = redisClient.smembers(key);
		end = System.currentTimeMillis();
		System.out.println("once use time " + (end - begin));
		Assert.assertEquals(result1.size(), result2.size());
		// 清空key
		redisClient.del(key);
	}

	@Test
	public void testDelLongList() {
		final byte[] key = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0 };
		JedisClientPool pool = RedisPoolManager.getJedisClientPool(RedisDBName.TALENT_REC);
		Jedis redisClient = pool.getResource();
		// 清空key
		redisClient.del(key);
		final int cnt = 10;
		// 往list中填充元素
		for (int i = 0; i < cnt; i++) {
			redisClient.rpush(key, new byte[] { (byte) 100 });
		}
		Assert.assertTrue(redisClient.llen(key) == cnt);

		long begin = System.currentTimeMillis();
		RedisTool.deleteLongList(redisClient, key);
		long end = System.currentTimeMillis();
		System.out.println("use time " + (end - begin));

		Assert.assertTrue(redisClient.llen(key) == 0);
		redisClient.del(key);
	}

	@Test
	public void testDelBigSet() {
		final byte[] key = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 1 };
		JedisClientPool pool = RedisPoolManager.getJedisClientPool(RedisDBName.TALENT_REC);
		Jedis redisClient = pool.getResource();
		// 清空key
		redisClient.del(key);
		Random rnd = new Random();
		final int cnt = 10;
		// 往set中填充元素
		for (int i = 0; i < cnt; i++) {
			redisClient.sadd(key, DataTransform.intToBytes(rnd.nextInt(), false));
		}
		Assert.assertTrue(RedisTool.traverseBigSet(redisClient, key).size() == cnt);

		long begin = System.currentTimeMillis();
		RedisTool.deleteBigSet(redisClient, key);
		long end = System.currentTimeMillis();
		System.out.println("use time " + (end - begin));

		Assert.assertTrue(RedisTool.traverseBigSet(redisClient, key).size() == 0);
		redisClient.del(key);
	}
}
