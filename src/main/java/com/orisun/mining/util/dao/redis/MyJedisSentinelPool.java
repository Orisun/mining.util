package com.orisun.mining.util.dao.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class MyJedisSentinelPool extends JedisSentinelPool implements JedisClientPool {

	public MyJedisSentinelPool(String masterName, Set<String> sentinels, GenericObjectPoolConfig poolConfig,
			int timeout) {
		super(masterName, sentinels, poolConfig, timeout);
	}

	public MyJedisSentinelPool(String masterName, Set<String> sentinels, GenericObjectPoolConfig poolConfig,
			int timeout, String passwd) {
		super(masterName, sentinels, poolConfig, timeout, passwd);
	}

	@Override
	public Jedis getResource() {
		Jedis jedis = null;
		try {
			jedis = super.getResource();
		} catch (Exception e) {
			jedis = super.getResource();// 哨兵模式不稳定，给一次重试的机会
		}
		return jedis;
	}

}
