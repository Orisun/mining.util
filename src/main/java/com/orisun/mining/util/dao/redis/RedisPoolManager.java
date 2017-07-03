package com.orisun.mining.util.dao.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @Description:
 * @Author orisun
 * @Date 2016年8月5日
 */
public class RedisPoolManager {
	private static Log logger = LogFactory.getLog(RedisPoolManager.class);

	public static class RedisPools {
		private RedisPool masterPool;
		private RedisPool slavePool;

		/**
		 * 主从模型
		 * 
		 * @param masterPool
		 * @param slavePool
		 */
		@Deprecated
		public RedisPools(RedisPool masterPool, RedisPool slavePool) {
			this.masterPool = masterPool;
			this.slavePool = slavePool;
		}

		/**
		 * 哨兵模式
		 * 
		 * @param masterPool
		 */
		public RedisPools(RedisPool masterPool) {
			this.masterPool = masterPool;
			this.slavePool = null;
		}

		/**
		 * 对外不可见
		 * 
		 * @return
		 */
		private RedisPool getMasterPool() {
			if (this.masterPool == null) {
				logger.fatal("redis master pool is null");
			}
			return this.masterPool;
		}

		/**
		 * 没有从库时返回主库（比如哨兵模式就没有从库）
		 * 
		 * @return
		 */
		@Deprecated
		public RedisPool getSlavePool() {
			if (this.slavePool != null) {
				return this.slavePool;
			} else {
				return getMasterPool();
			}
		}
	}

	private static Map<RedisDBName, RedisPools> nameMap = new ConcurrentHashMap<RedisDBName, RedisPools>();

	/**
	 * 根据Redis的主从配置文件，创建Redis实例
	 * 
	 * @param dbname
	 * @param masterConfigFile
	 * @param slaveConfigFile
	 */
	@Deprecated
	public static void configRedisPool(RedisDBName dbname, String masterConfigFile, String slaveConfigFile) {
		nameMap.put(dbname, new RedisPools(RedisPoolFactory.factory(masterConfigFile, false),
				RedisPoolFactory.factory(slaveConfigFile, false)));
	}

	/**
	 * 根据哨兵配置文件，创建redis实例
	 * 
	 * @param dbname
	 * @param sentinelConfigFile
	 */
	public static void configRedisPool(RedisDBName dbname, String sentinelConfigFile) {
		nameMap.put(dbname, new RedisPools(RedisPoolFactory.factory(sentinelConfigFile, true)));
	}

	/**
	 * 如果不是想获得从库，请直接调用{@link #getJedisClientPool(RedisDBName)}
	 * 
	 * @param dbname
	 * @return
	 */
	@Deprecated
	public static RedisPools getRedisPool(RedisDBName dbname) {
		if (dbname != null) {
			RedisPools redisPools = nameMap.get(dbname);
			if (redisPools == null) {
				logger.fatal("redis " + dbname + " not in redis poll!");
				return null;
			}
			return redisPools;
		}
		return null;
	}

	/**
	 * 获取redis连接池。<br>
	 * 如果是主从模式，则该方法获得是主库的连接池。如果想获取从库的连接池，请调用{@link #getRedisPool(RedisDBName)}
	 * 再调用{@link RedisPools.getSlavePool()}
	 * 
	 * @param dbname
	 * @return
	 */
	public static JedisClientPool getJedisClientPool(RedisDBName dbname) {
		if (dbname != null) {
			RedisPools redisPools = nameMap.get(dbname);
			if (redisPools == null) {
				logger.fatal("redis " + dbname + " not in redis poll!");
				return null;
			}
			return redisPools.getMasterPool().getRedis();
		}
		return null;
	}

}
