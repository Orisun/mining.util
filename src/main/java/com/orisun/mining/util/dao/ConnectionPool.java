package com.orisun.mining.util.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingDeque;

//import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库连接池
 * 
 * @Author:orisun
 * @Since:2015-6-3
 * @Version:1.0
 */
class ConnectionPool {

	private static Log logger = LogFactory.getLog(ConnectionPool.class);
	private final String jdbcDriver = "com.mysql.jdbc.Driver"; // 数据库驱动
	private final LinkedBlockingDeque<String> urlList;// 从队首取出元素后，再把它放到队尾，这样多数据源被均匀的使用
	private final String dbName;
	private final String dbUsername; // 数据库用户名
	private final String dbPassword; // 数据库用户密码
	private final int minConnections; // 连接池的初始大小,亦最少连接数
	private final int incrementalConnections;// 连接池自动增加的大小
	private final int maxConnections; // 连接池最大的大小。如果
	private final int queryTimeOut;// 查询超时,单位毫秒。通常mysql查询几毫秒就可以完成
	private LinkedBlockingDeque<PooledConnection> connections; // 存放所有连接的容器，必须是线程安全的。采用先进先出策略，保证每个连接被均匀地使用

	/**
	 * DB配置采用Builder模式
	 * 
	 * @Author:orisun
	 * @Since:2016-3-15
	 * @Version:1.0
	 */
	public static class ConnectionPoolConfigure {
		private final String urls;// 有多个库时用逗号分隔
		private final String dbName;
		private final String userName;
		private final String passwd;
		private int minConnections = 5; // 连接池的初始大小,亦最少连接数
		private int incrementalConnections = 5;// 每次新增多少个连接
		private int maxConnections = 100; // 连接池最大的大小。如果maxConnections为0或负数，表示连接数量没有限制
		private int queryTimeOut = 30;// 查询超时,单位毫秒。通常mysql查询几毫秒就可以完成

		public ConnectionPoolConfigure(String urls, String dbName,
				String userName, String passwd) {
			this.urls = urls;
			this.dbName = dbName;
			this.userName = userName;
			this.passwd = passwd;
		}

		public ConnectionPoolConfigure minConnections(int value) {
			this.minConnections = value;
			return this;
		}

		public ConnectionPoolConfigure incrementalConnections(int value) {
			this.incrementalConnections = value;
			return this;
		}

		public ConnectionPoolConfigure maxConnections(int value) {
			this.maxConnections = value;
			return this;
		}

		public ConnectionPoolConfigure queryTimeOut(int value) {
			this.queryTimeOut = value;
			return this;
		}

		public ConnectionPool build() {
			return new ConnectionPool(this);
		}
	}

	public ConnectionPool(ConnectionPoolConfigure configure) {
		this.dbName = configure.dbName;
		String[] dbUrls = configure.urls.split(",");
		this.urlList = new LinkedBlockingDeque<String>();
		for (String ele : dbUrls) {
			this.urlList.add(ele.trim());
		}
		this.dbUsername = configure.userName;
		this.dbPassword = configure.passwd;
		this.minConnections = configure.minConnections;
		this.incrementalConnections = configure.incrementalConnections;
		this.maxConnections = configure.maxConnections;
		this.queryTimeOut = configure.queryTimeOut;
		try {
			createPool();
		} catch (Exception e) {
			logger.error("create db connbection pool failed.", e);
		}
	}

	/**
	 * 创建一个数据库连接池
	 * 
	 * @throws Exception
	 */
	public synchronized void createPool() throws Exception {
		if (connections != null) {
			return;
		}
		// 实例化 JDBC Driver 中指定的驱动类实例
		Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
		// 注册 JDBC 驱动程序
		DriverManager.registerDriver(driver);
		// 保存连接的容器，用一个双端队列
		this.connections = new LinkedBlockingDeque<PooledConnection>();
		// 根据 initialConnections 中设置的值，创建连接
		createConnections(this.minConnections);
		logger.info("database connection pool have created.");
	}

	/**
	 * 创建由 numConnections 指定数目的数据库连接,并把这些连接放入connections向量中
	 * 
	 * @param numConnections
	 *            要创建的数据库连接的数目
	 */
	private void createConnections(int numConnections) throws SQLException {
		final double WARN_THRESH = 0.8;
		for (int x = 0; x < numConnections; x++) {
			if (this.maxConnections > 0) {
				int poolSize=connections.size();//size()方法要加锁，增删元素有操作都得阻塞，所以size()尽量少调用
				// 连接池达到最大限制时不再创建连接
				if(poolSize>= this.maxConnections) {
					logger.fatal("connection have reach the maximum, can not create more for "
							+ this.dbName);
					break;
				}
				// 连接池达到上限的80%时打印warn日志
				else if (poolSize >= this.maxConnections* WARN_THRESH) {
					DecimalFormat df = new DecimalFormat("##.##%");
					logger.fatal("pool size(" + poolSize
							+ ") have reach " + df.format(WARN_THRESH)
							+ " of the maximum capacity(" + this.maxConnections
							+ ") for " + this.dbName);
				}
			}

			try {
				// 新创建的连接放到队尾
				Connection newconn = newConnection();
				if (newconn != null) {
					connections.add(new PooledConnection(newconn,
							this.queryTimeOut));
				} else {
					x--;
				}
			} catch (SQLException e) {
				logger.error("create db connection failed", e);
				throw new SQLException();
			}
		}
		logger.info("have create " + connections.size() + " connections for db "
				+ this.dbName);
	}

	/**
	 * 创建一个新的数据库连接并返回它
	 * 
	 * @return 返回一个新创建的数据库连接
	 */
	private Connection newConnection() throws SQLException {
		String url = this.urlList.pollFirst();// 从队首取出
		Connection conn = null;
		if (url != null) {
			this.urlList.add(url);// 插入到队尾
			conn = DriverManager.getConnection("jdbc:mysql://" + url + "/"
					+ dbName, dbUsername, dbPassword);
		}
		return conn;
	}

	/**
	 * 获取一个可用的数据库连接<br>
	 * 如果当前没有可用的数据库连接，并且更多的数据库连接不能创建（ 如连接池大小的限制），此函数等待一会再尝试获取。
	 * 
	 * @return 返回一个可用的数据库连接对象,如果没有可用的连接则返回null
	 */

	public PooledConnection getConnection() throws SQLException {
		// 确保连接池己被创建
		if (connections == null) {
			return null;
		}
		PooledConnection conn = getFreeConnection(); // 获得一个可用的数据库连接
		// 如果目前没有可以使用的连接，即所有的连接都在使用中
		while (conn == null) {
			// 等一会再试
			wait(250);
			conn = getFreeConnection(); // 重新再试，直到获得可用的连接，如果
		}
		return conn;
	}

	private PooledConnection getFreeConnection() throws SQLException {
		// 从连接池中获得一个可用的数据库连接
		PooledConnection conn = findFreeConnection();
		if (conn == null) {
			// 如果目前连接池中没有可用的连接创建一些连接
			createConnections(incrementalConnections);
			// 重新从池中查找是否有可用连接
			conn = findFreeConnection();
		}
		return conn;
	}

	/**
	 * 查找连接池中所有的连接，查找一个可用的数据库连接，如果没有可用的连接，返回 null
	 * 
	 * @return 返回一个可用的数据库连接
	 */
	private PooledConnection findFreeConnection() throws SQLException {
		// 从队首取
		PooledConnection pc = this.connections.pollFirst();
		if (pc != null) {
			Connection conn = pc.getConnection();
			if (!isValid(conn)) {
				// 如果此连接不可再用了，则创建一个新的连接，并替换此不可用的连接对象
				try {
					conn = newConnection();
					if (conn == null) {
						pc = null;
					} else {
						pc.setConnection(conn);
					}
				} catch (SQLException e) {
					logger.error("create new db connection failed.", e);
					return null;
				}
			}
		}
		return pc;
	}

	/**
	 * 
	 * 测试一个连接是否可用，如果不可用，关掉它并返回 false,否则可用返回 true
	 * 
	 * @param conn
	 *            需要测试的数据库连接
	 * 
	 * @return 返回 true 表示此连接可用， false 表示不可用
	 */
	private boolean isValid(Connection conn) {
		try {
			/**
			 * 有些版本的MySQL JDBC存在bug，调用Connection.isValid()时会报错：
			 * Exception in thread "Thread-20" java.lang.RuntimeException: java.lang.NullPointerException
			 * at com.mysql.jdbc.JDBC4Connection$1$1.run(JDBC4Connection.java:106)
			 * Caused by: java.lang.NullPointerException
			 * at com.mysql.jdbc.ConnectionImpl.abortInternal(ConnectionImpl.java:1229)
			 * at com.mysql.jdbc.JDBC4Connection$1$1.run(JDBC4Connection.java:104)
			 */
			return conn.isValid(1);
		} catch (SQLException e) {
			logger.error("test connection validation failed", e);
			return false;
		}
	}

	/**
	 * 此函数返回一个数据库连接到连接池中<br>
	 * 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
	 * 
	 * @param conn 需返回到连接池中的连接对象
	 */
	public void returnConnection(PooledConnection conn) {
		// 确保连接池存在，如果连接没有创建（不存在），直接返回
		if (connections == null) {
			logger.error("no connection pool for put back");
			return;
		}
		// 放回时放到队尾
		this.connections.add(conn);
	}

	/**
	 * 
	 * 关闭连接池中所有的连接，并清空连接池。当系统要退出时才可调用此函数
	 */
	public synchronized void closeConnectionPool() throws SQLException {
		// 确保连接池存在，如果不存在，返回
		if (connections == null) {
			logger.error("no connection pool to close");
			return;
		}

		PooledConnection pc = null;
		while ((pc = this.connections.poll()) != null) {
			closeConnection(pc.getConnection());
		}
		// 置连接池为空
		connections = null;
		logger.info("release db connection pool for " + this.dbName);
	}

	/**
	 * 关闭一个数据库连接
	 * 
	 * @param conn 需要关闭的数据库连接
	 */
	public void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			logger.error("close db connection failed ", e);
		}
	}

	/**
	 * 回收连接池中空闲的连接
	 */
	public synchronized void recycle() throws SQLException {
		// 确保连接池己创新存在
		if (connections == null) {
			logger.error("no connection pool for refresh");
			return;
		}
		int closeNum = 0;
		// 回收时要保证池中的最少连接数
		while (this.connections.size() > this.minConnections) {
			// 从连接池中移除。从队尾取
			PooledConnection pc = this.connections.pollLast();
			if (pc != null) {
				closeNum++;
				// 把物理连接关闭掉
				Connection conn = pc.getConnection();
				closeConnection(conn);
			}
		}
		logger.info("recycle " + closeNum + " connections, now have "
				+ connections.size() + " connections in " + this.dbName
				+ " pool");

	}

	/**
	 * 使程序等待给定的毫秒数
	 * 
	 * @param mSeconds 给定的毫秒数
	 */

	private void wait(int mSeconds) {
		try {
			Thread.sleep(mSeconds);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 内部使用的用于保存连接池中连接对象的类。<br>
	 * 此类中有两个成员，一个是数据库的连接，另一个是指示此连接是否 正在使用的标志。
	 */
	public class PooledConnection {
		private Connection connection = null;// 数据库连接
		private int queryTimeOut;

		private PooledConnection(Connection connection, int queryTimeOut) {
			this.connection = connection;
			this.queryTimeOut = queryTimeOut;
		}

		public ResultSet executeQuery(String sql) throws SQLException {
			return connection.createStatement().executeQuery(sql);
		}

		public int executeUpdate(String sql) throws SQLException {
			return connection.createStatement().executeUpdate(sql);
		}

		public Connection getConnection() {
			return connection;
		}

		private void setConnection(Connection connection) {
			this.connection = connection;
		}

		public int getQueryTimeOut() {
			return queryTimeOut;
		}

		public void setQueryTimeOut(int queryTimeOut) {
			this.queryTimeOut = queryTimeOut;
		}
	}

	public String getDbName() {
		return dbName;
	}

}
