package com.orisun.mining.util.dao.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @Author orisun
 * @Date 2016年8月5日
 */
public class RedisPool {
	private static Log logger = LogFactory.getLog(RedisPool.class);
	private static AtomicLong usedTotal = new AtomicLong(0);// 库总共被使用了多少次，用于负载均衡

	private List<JedisClientPool> dbs = new ArrayList<JedisClientPool>();// 存放主库的容器

	public void addDb(JedisClientPool db) {
		dbs.add(db);
	}

	public JedisClientPool getRedis() {
		int len = dbs.size();
		if (len <= 0) {
			logger.fatal("capacity of redis pool is " + len);
			return null;
		}
		// 负载均衡策略采用最简单的轮询法
		long index = usedTotal.getAndIncrement() % len;
		// 达到MAX_LONG溢出时，再从0开始
		if (index < 0) {
			usedTotal.set(0L);
			index = usedTotal.getAndIncrement() % len;
		}
		return dbs.get((int) index);
	}
}
