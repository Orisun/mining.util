package com.orisun.mining.util.dao;

import com.orisun.mining.util.SystemConfig;
import com.orisun.mining.util.dao.ConnectionPool.PooledConnection;
import com.orisun.mining.util.dao.DaoHelperPool.ConnectionPools;
import com.orisun.mining.util.monitor.KVreport;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * 
 * @Author orisun
 * @Since 2015-9-29
 * @Version 1.0
 */
public class BaseDao<T, PK extends Serializable> {

	private static Log logger = LogFactory.getLog(BaseDao.class);
	protected final Class<T> aclass;
	protected final String TABLE;
	// SQL中的关键字，防SQL攻击
	private static Set<String> sqlKeywords = new HashSet<String>();
	private Map<String, Field> column2Field = new HashMap<String, Field>();// DB字段名=》类属性名
	private String allColumns = "";
	private String pkColumn = "id";// 默认情况下，主键的名称
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private static Set<Class<?>> validType = new HashSet<Class<?>>(); // 若要和DB类型对应，合法的java类型
	private static ExecutorService exec = Executors.newCachedThreadPool();
	private KVreport kvReporter = KVreport.getReporter();
	private int dbErrorKey = SystemConfig.getIntValue("db_error_key", -1);// 每次DB操作发生异常时上报
	private int dbTimeKey = SystemConfig.getIntValue("db_time_key", -1);// 每次的DB操作耗时都上报

	static {
		sqlKeywords.add("and");
		sqlKeywords.add("or");
		sqlKeywords.add("insert");
		sqlKeywords.add("select");
		sqlKeywords.add("delete");
		sqlKeywords.add("update");
		sqlKeywords.add("count");
		sqlKeywords.add("chr");
		sqlKeywords.add("mid");
		sqlKeywords.add("truncate");
		sqlKeywords.add("trunc");
		sqlKeywords.add("char");
		sqlKeywords.add("declare");
		sqlKeywords.add("like");
		sqlKeywords.add("%");
		sqlKeywords.add("<");
		sqlKeywords.add(">");
		sqlKeywords.add("=");
		sqlKeywords.add("\"");
		sqlKeywords.add("'");
		sqlKeywords.add(")");
		sqlKeywords.add("(");
		// 防止Xss攻击
		sqlKeywords.add("script");
		sqlKeywords.add("alert");

		validType.add(int.class);
		validType.add(Integer.class);
		validType.add(byte.class);
		validType.add(Byte.class);
		validType.add(Float.class);
		validType.add(float.class);
		validType.add(Short.class);
		validType.add(short.class);
		validType.add(Long.class);
		validType.add(long.class);
		validType.add(String.class);
		validType.add(Double.class);
		validType.add(double.class);
		validType.add(Date.class);
		validType.add(Timestamp.class);
	}

	/**
	 * 判断str中是否包含SQL关键字
	 * 
	 * @param str
	 * @return
	 */
	protected boolean containSql(String str) {
		String[] arr = str.split("\\s+");
		for (String ele : arr) {
			if (sqlKeywords.contains(ele)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public BaseDao() throws Exception {
		// 获得超类的泛型参数（即T和PK）的首元素的实际类型（即T在运行时对应的实际类型）
		this.aclass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (aclass.isAnnotationPresent(Table.class)) {
			Table table = (Table) aclass.getAnnotation(Table.class);
			String name = table.name();
			if (name != null) {
				this.TABLE = name;
			} else {
				this.TABLE = "";
			}
		} else {
			this.TABLE = "";
		}

		if (this.TABLE == null || "".equals(this.TABLE)) {
			throw new Exception("have not specify the table name for " + aclass.getCanonicalName());
		}
		Field[] fileds = aclass.getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			field.setAccessible(true);
			String columnName = field.getName();
			// 丢弃2种成员变量：静态和带NotColumn注解的
			if (!field.isAnnotationPresent(NotColumn.class)
					&& (field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
				if (field.isAnnotationPresent(Column.class)) {
					columnName = field.getAnnotation(Column.class).value();
				}
				if (field.isAnnotationPresent(Id.class)) {
					pkColumn = columnName;
					if (field.getAnnotation(Id.class).auto_increment() == true) {
						assert field.getType() == Integer.class || field.getType() == int.class
								|| field.getType() == Long.class || field.getType() == long.class;
					}
				}
				column2Field.put(columnName.toLowerCase(), field);
			}
		}
		allColumns = StringUtils.join(column2Field.keySet(), ",");
	}

	/**
	 * 获取一个主库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public PooledConnection getMasterConn() throws SQLException {
		ConnectionPools pools = DaoHelperPool.getConnPool(aclass);
		if (pools != null) {
			PooledConnection conn = pools.getMasterPool().getConnection();
			return conn;
		}
		return null;
	}

	/**
	 * 获取一个从库连接。从库连接没有时获取主库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public PooledConnection getSlaveConn() throws SQLException {
		ConnectionPools pools = DaoHelperPool.getConnPool(aclass);
		if (pools != null) {
			PooledConnection conn = pools.getSlavePool().getConnection();
			return conn;
		}
		return null;
	}

	/**
	 * 关闭一个从库的物理连接
	 * 
	 * @param conn
	 */
	private void closeSlaveConnection(PooledConnection conn) {
		ConnectionPools pools = DaoHelperPool.getConnPool(aclass);
		if (pools != null) {
			ConnectionPool pool = pools.getSlavePool();
			pool.closeConnection(conn.getConnection());
		}
	}

	/**
	 * 把主库连接返回连接池
	 * 
	 * @param conn
	 */
	public void retrunMasterConn(PooledConnection conn) {
		ConnectionPools pools = DaoHelperPool.getConnPool(aclass);
		if (pools != null) {
			ConnectionPool pool = pools.getMasterPool();
			pool.returnConnection(conn);
		}
	}

	/**
	 * 把从库连接返回连接池
	 * 
	 * @param conn
	 */
	public void retrunSlaveConn(PooledConnection conn) {
		ConnectionPools pools = DaoHelperPool.getConnPool(aclass);
		if (pools != null) {
			ConnectionPool pool = pools.getSlavePool();
			pool.returnConnection(conn);
		}
	}

	/**
	 * 分页读取数据<br>
	 * 注意：使用完ResultSet后一定要调用ResultSet.close()
	 * 
	 * @param columns
	 *            各列用逗号分隔，不区分大小写，允许使用"*"
	 * @param where
	 * @param pageNo
	 *            页数，编号从1始
	 * @param pageSize
	 *            每页的大小，即使数据库中有充足的数据，返回的量也可能略少于pageSize
	 * @return
	 */
	@Deprecated
	public ResultSet getListByPage(String columns, String where, int pageNo, int pageSize) {
		if (columns == null || columns.length() == 0) {
			return null;
		}
		if (columns.contains("*")) {
			columns = allColumns;
		}
		columns = columns.toLowerCase();
		if (pageNo * pageSize > 5000) {
			logger.error("pageNo*pageSize  can't more than 5000");
			return null;
		}
		PooledConnection conn = null;
		ResultSet resultSet = null;
		// 当数据库设置了主键自增时，select出的结果默认就是按主键递增排序好的
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(columns);
		sql.append(" from ");
		sql.append(TABLE);
		if (where != null && where.length() > 0) {
			sql.append(" where ");
			sql.append(where);
		}
		sql.append(" limit ");
		sql.append(pageSize * (pageNo - 1));
		sql.append(",");
		sql.append(pageSize);
		int timeout = 0;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getSlaveConn();
			timeout = conn.getQueryTimeOut();
			final Statement statement = conn.getConnection().createStatement();
			final String sqlF = sql.toString();
			Future<ResultSet> futureResult = exec.submit(new Callable<ResultSet>() {
				@Override
				public ResultSet call() throws Exception {
					return statement.executeQuery(sqlF);
				}
			});
			resultSet = futureResult.get(timeout, TimeUnit.MILLISECONDS);
			// 如果返回结果数为0，则返回的ResultSet为null
			resultSet.last();
			if (resultSet.getRow() == 0) {
				resultSet = null;
			} else {
				resultSet.beforeFirst();
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			logger.error("read data from " + TABLE + " failed", e);
			kvReporter.send(dbErrorKey, 1);
		} catch (TimeoutException e) {
			if (conn != null) {
				// 超时，则直接关闭物理连接
				this.closeSlaveConnection(conn);
			}
			logger.error("sql query timeout, SQL=" + sql.toString() + ", time limit is " + timeout);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			if (conn != null) {
				// 正常使用完，返还连接
				this.retrunSlaveConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return resultSet;
	}

	/**
	 * 分页读取数据
	 * 
	 * @param columns
	 *            各列用逗号分隔，不区分大小写，允许使用"*"
	 * @param where
	 * @param pageNo
	 *            页数，编号从1始
	 * @param pageSize
	 *            每页的大小，即使数据库中有充足的数据，返回的量也可能略少于pageSize
	 * @return 发生异常时返回null，通常是TimeoutException或SQLException
	 */
	public List<T> getDataByPage(String columns, String where, int pageNo, int pageSize) {
		return getDataByPage(columns, where, pageNo, pageSize, null, true);
	}

	/**
	 * 分页读取数据
	 * 
	 * @param columns
	 *            各列用逗号分隔，不区分大小写，允许使用"*"
	 * @param where
	 * @param pageNo
	 *            页数，编号从1始
	 * @param pageSize
	 *            每页的大小，即使数据库中有充足的数据，返回的量也可能略少于pageSize
	 * @param index
	 *            (不使用)使用的索引名称
	 * @param force
	 *            指定是使用还是不使用索引index
	 * @return 发生异常时返回null，通常是TimeoutException或SQLException
	 */
	public List<T> getDataByPage(String columns, String where, int pageNo, int pageSize, String index, boolean force) {
		List<T> rect = new ArrayList<T>();
		if (columns == null || columns.length() == 0) {
			return rect;
		}
		if (columns.contains("*")) {
			columns = allColumns;
		}
		columns = columns.toLowerCase();
		if (pageNo * pageSize > 5000) {
			logger.error("pageNo*pageSize  can't more than 5000");
			return rect;
		}
		PooledConnection conn = null;
		ResultSet resultSet = null;
		Set<String> columnSet = new HashSet<String>();
		String[] arr = columns.split(",");
		for (String col : arr) {
			col = col.trim();
			if (col.length() > 0) {
				columnSet.add(col);
			}
		}
		// 当数据库设置了主键自增时，select出的结果默认就是按主键递增排序好的
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(columns);
		sql.append(" from ");
		sql.append(TABLE);

		if (index != null && index.trim().length() > 0) {
			if (force) {
				sql.append(" force index(");
			} else {
				sql.append(" ignore index(");
			}
			sql.append(index.trim());
			sql.append(")");
		}

		if (where != null && where.length() > 0) {
			sql.append(" where ");
			sql.append(where);
		}
		sql.append(" limit ");
		sql.append(pageSize * (pageNo - 1));
		sql.append(",");
		sql.append(pageSize);
		int timeout = 0;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getSlaveConn();
			final Statement statement = conn.getConnection().createStatement();
			timeout = conn.getQueryTimeOut();
			final String sqlF = sql.toString();
			Future<ResultSet> futureResult = exec.submit(new Callable<ResultSet>() {
				@Override
				public ResultSet call() throws Exception {
					return statement.executeQuery(sqlF);
				}
			});
			resultSet = futureResult.get(timeout, TimeUnit.MILLISECONDS);
			while (resultSet.next()) {
				T inst = po2Vo(resultSet, columnSet);
				if (inst != null) {
					rect.add(inst);
				}
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			rect = null;
			logger.error("read data from " + TABLE + " failed", e);
			kvReporter.send(dbErrorKey, 1);
		} catch (TimeoutException e) {
			rect = null;
			if (conn != null) {
				// 超时则关闭DB连接，这样就会造成返回连接池中的有无效连接，从连接池中获取连接时需要判断一下连接是否可用。
				this.closeSlaveConnection(conn);
			}
			logger.error("sql query timeout, SQL=" + sql.toString() + ", time limit is " + timeout);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				logger.error("close ResultSet failed", e);
				kvReporter.send(dbErrorKey, 1);
			}
			if (conn != null) {
				// 正常使用完，返还连接
				this.retrunSlaveConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 分页读取数据
	 * 
	 * @param columns
	 *            各列用逗号分隔，不区分大小写，允许使用"*"
	 * @param where
	 * @param pageNo
	 *            页数，编号从1始
	 * @param pageSize
	 *            每页的大小，即使数据库中有充足的数据，返回的量也可能略少于pageSize
	 * @param forceIndex
	 *            显式指定要使用的索引名称
	 * @return 发生异常时返回null，通常是TimeoutException或SQLException
	 */
	@Deprecated
	public List<T> getDataByPage(String columns, String where, int pageNo, int pageSize, String forceIndex) {
		return getDataByPage(columns, where, pageNo, pageSize, forceIndex, true);
	}

	/**
	 * in查询
	 * 
	 * @param columns
	 *            要获取哪几列
	 * @param collections
	 * @param targetColumn
	 *            在哪一列上进行where in查询
	 * @return 发生异常时返回null
	 */
	public <K extends Number> List<T> getIn(String columns, Set<K> collections, String targetColumn) {
		List<T> rect = new ArrayList<T>();
		if (columns == null || columns.length() == 0 || targetColumn == null || targetColumn.length() == 0
				|| collections == null || collections.size() == 0) {
			return rect;
		}
		if (columns.contains("*")) {
			columns = allColumns;
		}
		columns = columns.toLowerCase();
		PooledConnection conn = null;
		ResultSet resultSet = null;
		Set<String> columnSet = new HashSet<String>();
		String[] arr = columns.split(",");
		for (String col : arr) {
			col = col.trim();
			if (col.length() > 0) {
				columnSet.add(col);
			}
		}
		// 当数据库设置了主键自增时，select出的结果默认就是按主键递增排序好的
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(columns);
		sql.append(" from ");
		sql.append(TABLE);
		sql.append(" where ");
		sql.append(targetColumn);
		sql.append(" in (");
		for (Number ele : collections) {
			sql.append(ele);
			sql.append(",");
		}
		sql.setCharAt(sql.length() - 1, ')');
		int timeout = 0;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getSlaveConn();
			final Statement statement = conn.getConnection().createStatement();
			timeout = conn.getQueryTimeOut();
			final String sqlF = sql.toString();
			Future<ResultSet> futureResult = exec.submit(new Callable<ResultSet>() {
				@Override
				public ResultSet call() throws Exception {
					return statement.executeQuery(sqlF);
				}

			});
			resultSet = futureResult.get(timeout, TimeUnit.MILLISECONDS);
			while (resultSet.next()) {
				T inst = po2Vo(resultSet, columnSet);
				if (inst != null) {
					rect.add(inst);
				}
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			rect = null;
			logger.error("read data from " + TABLE + " failed", e);
			kvReporter.send(dbErrorKey, 1);
		} catch (TimeoutException e) {
			rect = null;
			if (conn != null) {
				// 超时则关闭DB连接，这样就会造成返回连接池中的有无效连接，从连接池中获取连接时需要判断一下连接是否可用。
				this.closeSlaveConnection(conn);
			}
			logger.error("sql query timeout, SQL=" + sql.toString() + ", time limit is " + timeout);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				logger.error("close ResultSet failed", e);
				kvReporter.send(dbErrorKey, 1);
			}
			if (conn != null) {
				// 正常使用完，返还连接
				this.retrunSlaveConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 从迭代器ResultSet中读出一个实体
	 * 
	 * @param resultSet
	 * @return
	 */
	private T po2Vo(ResultSet resultSet, Set<String> columns) {
		try {
			@SuppressWarnings("unchecked")
			T inst = (T) Class.forName(aclass.getName()).newInstance();
			for (Entry<String, Field> entry : column2Field.entrySet()) {
				Field field = entry.getValue();
				String columnName = entry.getKey();
				if (columns.contains("*") || columns.contains(columnName)) {
					if (field.getType() == Integer.class || field.getType() == int.class) {
						field.set(inst, resultSet.getInt(columnName));
					} else if (field.getType() == Long.class || field.getType() == long.class) {
						field.set(inst, resultSet.getLong(columnName));
					} else if (field.getType() == Double.class || field.getType() == double.class) {
						field.set(inst, resultSet.getDouble(columnName));
					} else if (field.getType() == String.class) {
						field.set(inst, resultSet.getString(columnName));
					}
					// MySQL中的datetime和timestamp都只精确到秒，所以返回的毫秒数都是1000的整倍数
					else if (field.getType() == Date.class) {
						field.set(inst, new Date(resultSet.getTimestamp(columnName).getTime()));
					} else if (field.getType() == Short.class || field.getType() == short.class) {
						field.set(inst, resultSet.getShort(columnName));
					} else if (field.getType() == Timestamp.class) {
						field.set(inst, resultSet.getTimestamp(columnName));
					} else if (field.getType() == Byte.class || field.getType() == byte.class) {
						field.set(inst, resultSet.getByte(columnName));
					} else if (field.getType() == Float.class || field.getType() == float.class) {
						field.set(inst, resultSet.getFloat(columnName));
					}
				}
			}
			return inst;
		} catch (Exception e) {
			logger.error("parse column failed", e);
			kvReporter.send(dbErrorKey, 1);
			return null;
		}
	}

	/**
	 * 根据主键获得一个实体
	 * 
	 * @param id
	 * @return
	 */
	public T getById(PK id) {
		T rect = null;
		PooledConnection conn = null;
		ResultSet resultSet = null;
		String sql = "select " + allColumns + " from " + TABLE + " where " + pkColumn + "=" + id;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getSlaveConn();
			Statement statement = conn.getConnection().createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				rect = po2Vo(resultSet, column2Field.keySet());
			}
		} catch (SQLException e) {
			logger.error("read data from " + TABLE + " failed", e);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					logger.error("close ResultSet failed", e);
					kvReporter.send(dbErrorKey, 1);
				}
			}
			if (conn != null) {
				// 正常使用完，返还连接
				this.retrunSlaveConn(conn);
				;
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 根据条件删除某些记录<br>
	 * 注意删除的数据较多（大于100）时尽量按id删，否则很容易造成慢查询
	 * 
	 * @param condition
	 * @return 成功返回非负数，失败返回-1
	 */
	public int delete(String condition) {
		int rect = -1;

		String sql = "delete from " + TABLE + " where " + condition;
		PooledConnection conn = null;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getMasterConn();
			rect = conn.executeUpdate(sql);
		} catch (SQLException e) {
			logger.error("delete from " + TABLE + " failed, condition:" + condition, e);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			if (conn != null) {
				this.retrunMasterConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 删除指定column值属于集合ids的行<br>
	 * 注意：column必须是索引
	 * 
	 * @param ids
	 * @param column
	 * @return
	 */
	public int deleteIn(List<? extends Number> ids, String column) {
		int rect = -1;
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(TABLE);
		sql.append(" where ");
		sql.append(column);
		sql.append(" in (");
		for (Number ele : ids) {
			sql.append(ele);
			sql.append(",");
		}
		sql.setCharAt(sql.length() - 1, ')');
		PooledConnection conn = null;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getMasterConn();
			rect = conn.executeUpdate(sql.toString());
		} catch (SQLException e) {
			logger.error("delete from " + TABLE + " failed", e);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			if (conn != null) {
				this.retrunMasterConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 插入一条新记录
	 * 
	 * @param entity
	 * @return 成功返回受影响的行数，失败返回-1
	 */
	public int insert(T entity) {
		int rect = -1;
		StringBuilder columnNames = new StringBuilder();
		StringBuilder values = new StringBuilder();
		try {
			for (Entry<String, Field> entry : column2Field.entrySet()) {
				Field field = entry.getValue();
				if ((!field.isAnnotationPresent(Id.class) || field.getAnnotation(Id.class).auto_increment() == false)
						&& validType.contains(field.getType())) {
					String columnName = entry.getKey();
					columnNames.append(columnName);
					columnNames.append(",");
					Object value = field.get(entity);
					if (value == null) {
						values.append("null");
					} else if (field.getType() == Date.class || field.getType() == Timestamp.class) {
						values.append(sdf.format((Date) value));
					} else if (field.getType() == String.class) {
						String sv = (String) value;
						if (containSql(sv)) {
							logger.warn("danger! sql injection:" + sv);
							sv = "";
						}
						values.append("'" + sv + "'");
					} else {
						values.append(value);
					}
					values.append(",");
				}
			}

		} catch (Exception e) {
			logger.error("reflect " + entity.getClass().getCanonicalName() + " entity failed", e);
			kvReporter.send(dbErrorKey, 1);
		}

		if (columnNames.length() > 0) {
			StringBuilder sql = new StringBuilder("insert into ");
			sql.append(TABLE);
			sql.append(" (");
			sql.append(columnNames.subSequence(0, columnNames.length() - 1));
			sql.append(") values (");
			sql.append(values.subSequence(0, values.length() - 1));
			sql.append(")");
			PooledConnection conn = null;
			long begin = System.currentTimeMillis();
			try {
				conn = this.getMasterConn();
				rect = conn.executeUpdate(sql.toString());
			} catch (Exception e) {
				// 如果 是因为唯一键值冲突，则不打印日志
				if (!e.getMessage().contains("Duplicate entry")) {
					logger.error("insert data into " + TABLE + " failed", e);
					kvReporter.send(dbErrorKey, 1);
				}
				rect = -1;
			} finally {
				if (conn != null) {
					this.retrunMasterConn(conn);
				}
				long end = System.currentTimeMillis();
				kvReporter.send(dbTimeKey, end - begin);
			}
		}
		return rect;
	}

	/**
	 * 批量插入数据
	 * 
	 * @param entities
	 * @return 成功返回受影响的行数，失败返回-1
	 */
	public int batchInsert(List<T> entities) {
		if (entities == null || entities.size() == 0) {
			return 0;
		}
		int rect = -1;
		StringBuilder sqlBuffer = new StringBuilder("insert into ");
		sqlBuffer.append(TABLE);
		StringBuilder columnNames = new StringBuilder();
		for (Entry<String, Field> entry : column2Field.entrySet()) {
			Field field = entry.getValue();
			if ((!field.isAnnotationPresent(Id.class) || field.getAnnotation(Id.class).auto_increment() == false)
					&& validType.contains(field.getType())) {
				String columnName = entry.getKey();
				columnNames.append(columnName);
				columnNames.append(",");
			}
		}
		sqlBuffer.append(" (");
		sqlBuffer.append(columnNames.subSequence(0, columnNames.length() - 1));
		sqlBuffer.append(") values ");

		for (T entity : entities) {
			StringBuilder values = new StringBuilder();
			try {
				for (Entry<String, Field> entry : column2Field.entrySet()) {
					Field field = entry.getValue();
					if ((!field.isAnnotationPresent(Id.class)
							|| field.getAnnotation(Id.class).auto_increment() == false)
							&& validType.contains(field.getType())) {
						Object value = field.get(entity);
						if (value == null) {
							values.append("null");
						} else if (field.getType() == Date.class || field.getType() == Timestamp.class) {
							values.append(sdf.format((Date) value));
						} else if (field.getType() == String.class) {
							String sv = (String) value;
							if (containSql(sv)) {
								logger.warn("danger! sql injection:" + sv);
								sv = "";
							}
							values.append("'" + sv + "'");
						} else {
							values.append(value);
						}
						values.append(",");
					}
				}

			} catch (Exception e) {
				logger.error("reflect " + entity.getClass().getCanonicalName() + " entity failed", e);
				kvReporter.send(dbErrorKey, 1);
			}

			if (values.length() > 0) {
				sqlBuffer.append("(");
				sqlBuffer.append(values.subSequence(0, values.length() - 1));
				sqlBuffer.append("),");
			}
		}
		PooledConnection conn = null;
		String sql = sqlBuffer.substring(0, sqlBuffer.length() - 1).toString();
		long begin = System.currentTimeMillis();
		try {
			conn = this.getMasterConn();
			rect = conn.executeUpdate(sql);
		} catch (Exception e) {
			// 如果是因为唯一键(包括主键在内)值冲突，则不打印日志
			if (e.getMessage() == null || !e.getMessage().contains("Duplicate entry")) {
				logger.error("insert data into " + TABLE + " failed, sql=" + sql, e);
				kvReporter.send(dbErrorKey, 1);
			}
			rect = -1;
		} finally {
			if (conn != null) {
				this.retrunMasterConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 根据主键更新一条记录
	 * 
	 * @param entity
	 * @return 如果记录不存在则返回0，更新成功返回正数，更新失败返回-1
	 */
	public int update(T entity) {
		int rect = -1;
		List<String> columnNames = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		String condition = null;
		try {
			for (Entry<String, Field> entry : column2Field.entrySet()) {
				Field field = entry.getValue();
				String columnName = entry.getKey();
				Object value = field.get(entity);
				if (field.isAnnotationPresent(Id.class)) {
					if (value == null) {
						return 0;
					}
					if (field.getType() == Date.class || field.getType() == Timestamp.class) {
						condition = columnName + "=" + sdf.format((Date) value);
					} else if (field.getType() == String.class) {
						String sv = (String) value;
						if (containSql(sv)) {
							logger.warn("danger! sql injection:" + sv);
							sv = "";
						}
						condition = columnName + "='" + sv + "'";
					} else {
						condition = columnName + "=" + value;
					}
				} else if (validType.contains(field.getType())) {
					if (value != null) {
						columnNames.add(columnName);
						if (field.getType() == Date.class || field.getType() == Timestamp.class) {
							values.add(sdf.format((Date) value));
						} else if (field.getType() == String.class) {
							String sv = (String) value;
							if (containSql(sv)) {
								logger.warn("danger! sql injection:" + sv);
								sv = "";
							}
							values.add("'" + sv + "'");
						} else {
							values.add(value.toString());
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("reflect " + entity.getClass().getCanonicalName() + " entity failed", e);
			kvReporter.send(dbErrorKey, 1);
		}

		if (columnNames.size() > 0 && condition != null) {
			StringBuilder sql = new StringBuilder("update ");
			sql.append(TABLE);
			sql.append(" set ");
			int i = 0;
			for (; i < columnNames.size() - 1; i++) {
				sql.append(columnNames.get(i) + "=" + values.get(i) + ",");
			}
			sql.append(columnNames.get(i) + "=" + values.get(i));
			sql.append(" where " + condition);
			PooledConnection conn = null;
			long begin = System.currentTimeMillis();
			try {
				conn = this.getMasterConn();
				rect = conn.executeUpdate(sql.toString());
			} catch (Exception e) {
				logger.error("update " + TABLE + " failed", e);
				kvReporter.send(dbErrorKey, 1);
				rect = -1;
			} finally {
				if (conn != null) {
					this.retrunMasterConn(conn);
				}
				long end = System.currentTimeMillis();
				kvReporter.send(dbTimeKey, end - begin);
			}
		}
		return rect;
	}

	/**
	 * 根据主键删除一条记录
	 * 
	 * @param id
	 * @return
	 */
	public int deleteById(PK id) {
		String where = pkColumn + "=" + id;
		return delete(where);
	}

	/**
	 * 根据where条件进行count
	 * 
	 * @param condition
	 * @return
	 */
	public int count(String condition) {
		int rect = -1;

		String sql = "select count(*) from " + TABLE + " where " + condition;
		PooledConnection conn = null;
		long begin = System.currentTimeMillis();
		try {
			conn = this.getSlaveConn();
			ResultSet resultSet = conn.executeQuery(sql);
			if (resultSet != null) {
				if (resultSet.next()) {
					rect = resultSet.getInt(1); // 编号从1开始
				}
				resultSet.close();
			}
		} catch (SQLException e) {
			logger.error("count(*) from " + TABLE + " failed, condition:" + condition, e);
			kvReporter.send(dbErrorKey, 1);
		} finally {
			if (conn != null) {
				this.retrunSlaveConn(conn);
			}
			long end = System.currentTimeMillis();
			kvReporter.send(dbTimeKey, end - begin);
		}
		return rect;
	}

	/**
	 * 慎用：慢查询，容易造成DB阻塞！<br>
	 * 删除表里的所有数据
	 * 
	 * @return
	 */
	@Deprecated
	public int deleteAll() {
		return delete("1=1");
	}
}
