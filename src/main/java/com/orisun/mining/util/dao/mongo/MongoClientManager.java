package com.orisun.mining.util.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.StringUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MongoClientManager {

	private static Log logger = LogFactory.getLog(MongoClientManager.class);

	private static Map<Class<?>, MongoClient> clazzMap = new ConcurrentHashMap<Class<?>, MongoClient>();
	private static Map<MongoDBName, MongoClient> nameMap = new ConcurrentHashMap<MongoDBName, MongoClient>();

	/**
	 * 指定Mongo配置文件
	 * 
	 * @param dbname
	 * @param configFile
	 */
	public static void configMongo(MongoDBName dbname, String configFile) {
		MongoClient mongo = factory(configFile);
		if (mongo != null) {
			nameMap.put(dbname, mongo);
		}
	}

	/**
	 * 获得一个MongoClient
	 * 
	 * @param clazz
	 * @return
	 */
	public static MongoClient getMongoClient(Class<?> clazz) {
		MongoClient mongoClient = clazzMap.get(clazz);
		if (mongoClient != null)
			return mongoClient;

		if (clazz.isAnnotationPresent(MongoDataBase.class)) {
			MongoDataBase dataBase = (MongoDataBase) clazz.getAnnotation(MongoDataBase.class);
			MongoDBName name = dataBase.name();
			if (name != null) {
				mongoClient = nameMap.get(name);
				if (mongoClient == null) {
					logger.fatal("mongodb " + name.name() + " not in mongo poll!");
					return null;
				}
				clazzMap.put(clazz, mongoClient);
				return mongoClient;
			}
		}
		return null;
	}

	/**
	 * 根据配置文件产生一个MongoClient
	 * 
	 * @param dbConfFile
	 * @return
	 */
	private static MongoClient factory(final String dbConfFile) {
		if (StringUtil.isBlank(dbConfFile)) {
			return null;
		}
		logger.info("initialize mongodb, path=" + dbConfFile);
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(dbConfFile));
			String[] servers = property.getProperty("server").split(",");
			int port = Integer.parseInt(property.getProperty("port", "27017"));
			int poolSize = Integer.parseInt(property.getProperty("max_conn", "100"));
			int connTimeout = Integer.parseInt(property.getProperty("conn_timeout", "100"));
			int waitTimeout = Integer.parseInt(property.getProperty("wait_timeout", "100"));
			MongoClientOptions mongoOptions = new MongoClientOptions.Builder().connectionsPerHost(poolSize) // 连接池中的最大连接数
					.threadsAllowedToBlockForConnectionMultiplier(poolSize * 2) // 等待队列的长度
					.connectTimeout(connTimeout)// 建立连接的超时时间
					.maxWaitTime(waitTimeout)// 等待连接的超时时间
					.build();

			List<ServerAddress> addresses = new ArrayList<ServerAddress>();
			for (String server : servers) {
				server = server.trim();
				if (server.length() > 0) {
					logger.info("connect to mongo on " + server + ":" + port);
					addresses.add(new ServerAddress(server, port));
				}
			}
			MongoClient mongoClient = null;
			/**
			 * mongod必须是复制集的形式，才支持readPreference这个选项。研发环境上的mongod只有一台，不支持复制集
			 */
			if (addresses.size() >= 2) {
				/**
				 * MongoDB五种读写分离的模式： primary主节点，默认模式，读操作只在主节点，如果主节点不可用，报错或者抛出异常。
				 * primaryPreferred首选主节点，大多情况下读操作在主节点，如果主节点不可用，如故障转移， 读操作在从节点。
				 * secondary从节点，读操作只在从节点， 如果从节点不可用，报错或者抛出异常。
				 * secondaryPreferred首选从节点，大多情况下读操作在从节点，特殊情况（如单主节点架构） 读操作在主节点。
				 * nearest最邻近节点，读操作在最邻近的成员，可能是主节点或者从节点。 <br>
				 * 官方不推荐从从节点上读数据，因为从节点上的数据可能不是最新的，个别场景除外，
				 * 详见http://docs.mongoing.com/manual-zh/core/read-preference.
				 * html#use-cases 复制集并不是为了提高读性能而存在的，如果想提升读性能，那么请使用索引和分片。
				 */
				mongoOptions = new MongoClientOptions.Builder(mongoOptions)
						.readPreference(ReadPreference.primaryPreferred()).build();
				mongoClient = new MongoClient(addresses, mongoOptions);
			} else if (addresses.size() == 1) {
				mongoClient = new MongoClient(addresses.get(0), mongoOptions);
			} else {
				logger.fatal("have not config mongo server address");
			}
			return mongoClient;
		} catch (FileNotFoundException e) {
			logger.fatal("db config file " + dbConfFile + " not found.");
		} catch (IOException e) {
			logger.fatal("parse db config file failed.", e);
		}
		return null;
	}
}
