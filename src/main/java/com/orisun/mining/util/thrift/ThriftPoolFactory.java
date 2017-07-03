package com.orisun.mining.util.thrift;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;
import org.jsoup.helper.StringUtil;

/**
 * 
 * 
 * @author orisun
 * @date 2017年2月13日
 */
public class ThriftPoolFactory {
	private static Log logger = LogFactory.getLog(ThriftPoolFactory.class);

	// 把已经创建过的ThriftPool都缓存起来，避免多次创建
	private static Map<String, ThriftPool> poolMap = new ConcurrentHashMap<String, ThriftPool>();

	/**
	 * 根据一个配置文件创建一个ThriftPool
	 * 
	 * @param confFile
	 * @return
	 */
	static ThriftPool factory(final String confFile) {
		if (StringUtil.isBlank(confFile)) {
			return null;
		}
		if (poolMap.containsKey(confFile)) {
			return poolMap.get(confFile);
		}
		logger.info("initialize thrift server, path=" + confFile);
		ThriftPool rect = null;
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(confFile));
			String[] servers = property.getProperty("server").split(",");
			int port = Integer.parseInt(property.getProperty("port"));
			rect = new ThriftPool();
			for (String server : servers) {
				GenericObjectPoolConfig config = new GenericObjectPoolConfig();
				// 最大空闲连接
				config.setMaxIdle(Integer.parseInt(property.getProperty("max_idle", "1")));
				// 最大连接数
				config.setMaxTotal(Integer.parseInt(property.getProperty("max_conn", "10")));
				// 在获取连接的时候检查有效性
				config.setTestOnBorrow(true);
				// 在空闲时检查有效性
				config.setTestWhileIdle(true);
				ObjectPool<TProtocol> pool = new AutoClearedGenericObjectPool<>(
						new TProtocolFactory(server, port, true), config);
				rect.addPool(pool);
			}
		} catch (FileNotFoundException e) {
			logger.fatal("thrift config file " + confFile + " not found.");
		} catch (IOException e) {
			logger.fatal("parse thrift config file failed.", e);
		}
		return rect;
	}
}
