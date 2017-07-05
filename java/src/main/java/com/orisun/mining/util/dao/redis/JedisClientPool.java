package com.orisun.mining.util.dao.redis;

import redis.clients.jedis.Jedis;

public interface JedisClientPool {

	public Jedis getResource();
}
