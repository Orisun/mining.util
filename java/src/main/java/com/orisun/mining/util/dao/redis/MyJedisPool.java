package com.orisun.mining.util.dao.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MyJedisPool extends JedisPool implements JedisClientPool {

	public MyJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout) {
		super(poolConfig, host, port, timeout);
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
