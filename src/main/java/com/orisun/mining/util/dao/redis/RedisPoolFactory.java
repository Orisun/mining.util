package com.orisun.mining.util.dao.redis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.StringUtil;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @Description:
 * @Author orisun
 * @Date 2016年8月5日
 */
public class RedisPoolFactory {

	private static Log logger = LogFactory.getLog(RedisPoolFactory.class);

	// 把已经创建过的RedisPool都缓存起来，避免多次创建
	private static Map<String, RedisPool> poolMap = new ConcurrentHashMap<String, RedisPool>();

	/**
	 * 根据一个配置文件创建一个RedisPool
	 * 
	 * @param dbConfFile
	 * @return
	 */
	static RedisPool factory(final String dbConfFile, boolean sentinel) {
		if (StringUtil.isBlank(dbConfFile)) {
			return null;
		}
		if (poolMap.containsKey(dbConfFile)) {
			return poolMap.get(dbConfFile);
		}
		logger.info("initialize redis pool, path=" + dbConfFile);
		RedisPool rect = new RedisPool();
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(dbConfFile));
			if (sentinel) {
				if (property.getProperty("master") != null && property.get("sentinel") != null) {
					String masterName = property.getProperty("master").trim();
					String[] sentinels = property.getProperty("sentinel").trim().split(",");
					Set<String> sentinelSet = new HashSet<String>();
					for (String ele : sentinels) {
						sentinelSet.add(ele);
					}
					String passwd = property.getProperty("auth", "").trim();
					// 读写响应耗时设置为20秒（主要是flushdb很耗时）。不配置默认是2秒
					final int soTimeout = Integer.parseInt(property.getProperty("sotimeout", "20000").trim());
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					// 最大空闲连接
					poolConfig.setMaxIdle(Integer.parseInt(property.getProperty("max_idle", "100").trim()));
					// 最大连接数
					poolConfig.setMaxTotal(Integer.parseInt(property.getProperty("max_conn", "300").trim()));
					// 获取连接的最大等等毫秒数
					poolConfig.setMaxWaitMillis(Long.parseLong(property.getProperty("max_wait", "100000").trim()));
					// 在获取连接的时候检查有效性
					poolConfig.setTestOnBorrow(false);// 如果设为true则报错：Could not
														// get a
														// resource from the
														// pool，Unable
														// to validate object
					// 在空闲时检查有效性
					poolConfig.setTestWhileIdle(true);
					MyJedisSentinelPool sentinelPool = null;
					// 没有密码
					if (StringUtil.isBlank(passwd)) {
						sentinelPool = new MyJedisSentinelPool(masterName, sentinelSet, poolConfig, soTimeout);
					}
					// 有密码
					else {
						sentinelPool = new MyJedisSentinelPool(masterName, sentinelSet, poolConfig, soTimeout, passwd);
					}
					rect.addDb(sentinelPool);
				} else {
					logger.fatal("no master or sentinel found");
					rect = null;
				}
			} else {
				String[] servers = property.getProperty("server").trim().split(",");
				final int port = Integer.parseInt(property.getProperty("port", "6379").trim());
				// 读写响应耗时设置为20秒（主要是flushdb很耗时）。不配置默认是2秒
				final int soTimeout = Integer.parseInt(property.getProperty("sotimeout", "20000").trim());
				for (String server : servers) {
					JedisPoolConfig config = new JedisPoolConfig();
					// 最大空闲连接
					config.setMaxIdle(Integer.parseInt(property.getProperty("max_idle", "100").trim()));
					// 最大连接数
					config.setMaxTotal(Integer.parseInt(property.getProperty("max_conn", "300").trim()));
					// 获取连接的最大等等毫秒数
					config.setMaxWaitMillis(Long.parseLong(property.getProperty("max_wait", "100000").trim()));
					// 在获取连接的时候检查有效性
					config.setTestOnBorrow(false);// 如果设为true则报错：Could not get a
													// resource from the
													// pool，Unable
													// to validate object
					// 在空闲时检查有效性
					config.setTestWhileIdle(true);
					MyJedisPool pool = new MyJedisPool(config, server, port, soTimeout);
					if (pool != null) {
						logger.info("create jedis pool on " + server + ":" + port);
						rect.addDb(pool);
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.fatal("db config file " + dbConfFile + " not found.");
			rect = null;
		} catch (Exception e) {
			logger.fatal("parse db config file failed.", e);
			rect = null;
		}
		return rect;
	}

}
