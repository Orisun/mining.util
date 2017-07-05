package com.orisun.mining.util.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @Author:orisun
 * @Since:2015-9-29
 * @Version:1.0
 */
public class DaoHelperPool {

	public static class ConnectionPools {
		private ConnectionPool masterPool;
		private ConnectionPool slavePool;

		public ConnectionPools(ConnectionPool masterPool,
				ConnectionPool slavePool) {
			this.masterPool = masterPool;
			this.slavePool = slavePool;
		}

		public ConnectionPool getMasterPool() {
			return this.masterPool;
		}

		/**
		 * 没有从库时返回主库
		 * 
		 * @return
		 */
		public ConnectionPool getSlavePool() {
			if (this.slavePool != null) {
				return this.slavePool;
			} else {
				return this.masterPool;
			}
		}
	}

	private static Map<Class<?>, ConnectionPools> clazzMap = new ConcurrentHashMap<Class<?>, ConnectionPools>();
	private static Map<DBName, ConnectionPools> map = new ConcurrentHashMap<DBName, ConnectionPools>();

	/**
	 * 由一个表实体获取其对应的ConnectionPool
	 * 
	 * @param clazz
	 * @return
	 */
	public static ConnectionPools getConnPool(Class<?> clazz) {
		ConnectionPools connPool = clazzMap.get(clazz);
		if (connPool != null)
			return connPool;

		if (clazz.isAnnotationPresent(DataBase.class)) {
			DataBase dataBase = (DataBase) clazz.getAnnotation(DataBase.class);
			DBName name = dataBase.name();
			if (name != null) {
				connPool = map.get(name);
				if (connPool == null) {
					throw new RuntimeException("dao:" + name.name()
							+ " not in connection poll!");
				}
				clazzMap.put(clazz, connPool);
				return connPool;
			}
		}
		return null;
	}

	/**
	 * 给数据库指定主库和从库的配置文件
	 * 
	 * @param dbname
	 * @param masterConfigFile
	 *            如果项目中没有用到主库，则masterConfigFile设为null
	 * @param slaveConfigFile
	 *            如果项目中没有用到从库，则slaveConfigFile设为null
	 */
	public static void configDb(DBName dbname, String masterConfigFile,
			String slaveConfigFile) {
		map.put(dbname,
				new ConnectionPools(ConnPoolFactory.factory(masterConfigFile),
						ConnPoolFactory.factory(slaveConfigFile)));
	}
}
