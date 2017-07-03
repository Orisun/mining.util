package com.orisun.mining.util.thrift;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.orisun.mining.util.Release;

/**
 * 
 * 
 * @author orisun
 * @date 2017年2月13日
 */
public class ThriftPoolManager extends Release {
	private static Log logger = LogFactory.getLog(ThriftPoolManager.class);

	private static Map<ThriftServerName, ThriftPool> nameMap = new ConcurrentHashMap<ThriftServerName, ThriftPool>();

	public static void configThriftPool(ThriftServerName dbname, String configFile) {
		ThriftPool tp = ThriftPoolFactory.factory(configFile);
		if (tp != null) {
			nameMap.put(dbname, tp);
		}
	}

	public static ThriftPool getThriftPool(ThriftServerName name) {
		if (name != null) {
			ThriftPool thriftServer = nameMap.get(name);
			if (thriftServer == null) {
				logger.fatal("thrift " + name + " not in thrift poll!");
				return null;
			}
			return thriftServer;
		}
		return null;
	}

	@Override
	public void releaseResource() {
		for (Entry<ThriftServerName, ThriftPool> entry : nameMap.entrySet()) {
			try {
				entry.getValue().getPoolAndProtocol().first.close();
			} catch (Exception e) {

			}
		}
		logger.info("closed all thrift pool");
	}
}
