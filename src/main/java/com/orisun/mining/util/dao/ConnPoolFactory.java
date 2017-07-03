package com.orisun.mining.util.dao;

import com.orisun.mining.util.Release;
import com.orisun.mining.util.dao.ConnectionPool.ConnectionPoolConfigure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.StringUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * DB连接池工厂类。一个DB创建一个连接池
 * 
 * @Author:orisun
 * @Since:2015-6-3
 * @Version:1.0
 */
class ConnPoolFactory extends Release {

	private static Log logger = LogFactory.getLog(ConnPoolFactory.class);
	private static Map<String, ConnectionPool> poolMap = new ConcurrentHashMap<String, ConnectionPool>();
	private static ScheduledExecutorService exec = Executors
			.newScheduledThreadPool(20);// 系统中使用的数据库不能超过20个

	/**
	 * 根据一个配置文件创建一个连接池
	 * 
	 * @param path
	 * @return
	 */
	static ConnectionPool factory(final String path) {
		if (StringUtil.isBlank(path)) {
			return null;
		}
		if (poolMap.containsKey(path)) {
			return poolMap.get(path);
		}
		logger.info("initialize db connection pool, path=" + path);
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(path));
			Map<String, String> configMap = new HashMap<String, String>();
			Iterator<Map.Entry<Object, Object>> it = property.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<Object, Object> entry = it.next();
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				configMap.put(key, value);
			}
			/**
			 * 配置文件中必须配置这4项：db_url,db_name,db_user,db_passwd
			 */
			String url = configMap.get("db_url");
			String dbName = configMap.get("db_name");
			String user = configMap.get("db_user");
			String passwd = configMap.get("db_passwd");
			ConnectionPoolConfigure configure = new ConnectionPoolConfigure(
					url, dbName, user, passwd);
			int refreshInterval = 10; // 每隔多少分钟回收一次池中的空闲连接
			if (configMap.containsKey("db_maxconn")) {
				int maxconn = Integer.parseInt(configMap.get("db_maxconn"));
				configure.maxConnections(maxconn);
			}
			if (configMap.containsKey("query_timeout")) {
				int queryTimeOut = Integer.parseInt(configMap
						.get("query_timeout"));
				configure.queryTimeOut(queryTimeOut);
			}
			if (configMap.containsKey("db_minconn")) {
				int minConn = Integer.parseInt(configMap.get("db_minconn"));
				configure.minConnections(minConn);
			}
			if (configMap.containsKey("inc_conn")) {
				int incConn = Integer.parseInt(configMap.get("inc_conn"));
				configure.incrementalConnections(incConn);
			}
			if (configMap.containsKey("refresh_interval")) {
				refreshInterval = Integer.parseInt(configMap
						.get("refresh_interval"));
			}
			final ConnectionPool pool = configure.build();
			poolMap.put(path, pool);

			// 开启定时任务，回收DB连接池中空闲的连接
			exec.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					try {
						pool.recycle();
					} catch (SQLException e) {
						logger.fatal("recycle db pool " + path + " failed", e);
					}
				}
			}, refreshInterval, refreshInterval, TimeUnit.MINUTES);

			return pool;
		} catch (FileNotFoundException e) {
			logger.fatal("db config file " + path + " not found.");
		} catch (IOException e) {
			logger.fatal("parse db config file failed.", e);
		}
		return null;
	}

	@Override
	public void releaseResource() {
		for (Entry<String, ConnectionPool> entry : poolMap.entrySet()) {
			String confFile = entry.getKey();
			ConnectionPool pool = entry.getValue();
			try {
				pool.closeConnectionPool();
				logger.info("release connection pool of mysqlDB "
						+ pool.getDbName());
			} catch (SQLException e) {
				logger.error(
						"close connection pool of " + confFile + " failed", e);
			}
		}
	}
}
