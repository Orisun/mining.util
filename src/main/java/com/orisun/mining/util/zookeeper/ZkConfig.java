package com.orisun.mining.util.zookeeper;

import com.orisun.mining.util.ClassUtil;
import com.orisun.mining.util.SystemConfig;
import com.orisun.mining.util.monitor.SendMail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @Author:orisun
 * @Since:2016-4-7
 * @Version:1.0
 */
public abstract class ZkConfig {

	private static Log logger = LogFactory.getLog(ZkConfig.class);
	private static final String ZK_PARAM_NAME = ZkParam.class.getCanonicalName();
	private Class<?> zkArgClz = null;
	private static Method getZkPathMethod = null;
	private static Method getIdMethod = null;
	private static Method getValueMethod = null;
	private ExecutorService exec = null;

	public ZkConfig() {
		exec = Executors.newCachedThreadPool();
		try {
			zkArgClz = Class.forName(ZK_PARAM_NAME);
			getZkPathMethod = zkArgClz.getMethod("getPath");
			getIdMethod = zkArgClz.getMethod("getLogicid");
			getValueMethod = zkArgClz.getMethod("getValue");
		} catch (Exception e) {
			logger.fatal("build " + ZK_PARAM_NAME + " failed", e);
			System.exit(1);
		}
	}

	public void updateParam(String filedName, ZkParam newArgument) {
		try {
			Method method = this.getClass().getMethod(ClassUtil.parseSetName(filedName), ZkParam.class);
			method.invoke(this, newArgument);
			logger.info("set " + filedName + " to " + newArgument.getValue());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.error("zookeeper node is changed, but update system parameter failed", e);
		}
	}

	/**
	 * 添加zookeeper监听，参数变化时及时反应到推荐系统中
	 */
	@SuppressWarnings("resource")
	private void addListener() {
		final CuratorFramework zkClient = ZkClient.getInstance().getZkClient();
		try {
			Field[] fields = this.getClass().getDeclaredFields();// 父类中的成员获取不到
			for (final Field field : fields) {
				field.setAccessible(true);
				if (field.getType().getCanonicalName().equals(ZK_PARAM_NAME)) {
					Object zkParamInst = field.get(this);
					final String path = (String) getZkPathMethod.invoke(zkParamInst);
					if (zkClient.checkExists().forPath(path) != null) {
						final int logicid = (int) getIdMethod.invoke(zkParamInst);
						NodeCache nodeCache = new NodeCache(zkClient, path, false);
						nodeCache.start(true);
						nodeCache.getListenable().addListener(new NodeCacheListener() {
							@Override
							public void nodeChanged() throws Exception {
								Thread.sleep(1000);// 故意等1秒，确保下一行代码读出来的是更新之后的值
								byte[] brr = zkClient.getData().forPath(path);
								double newValue = Double.parseDouble(new String(brr));
								ZkParam newArgument = new ZkParam(newValue, path, logicid);
								updateParam(field.getName(), newArgument);
							}
						}, exec);
						logger.info("add listener to " + path);
					} else {
						logger.error("will add listner on zookeeper path " + path + ", but it dose not exists");
					}
				}
			}
		} catch (Exception e) {
			logger.error("add listener to zookeeper failed", e);
			SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
					SystemConfig.getValue("mail_receiver"), "add listener to zookeeper failed<br>" + e.getMessage());
		}
	}

	/**
	 * 每天定时任务，从MySQL中读取参数的值及参数对应的zkpath，然后监听该zkpath。<br>
	 * 这是为了防止zookeeper连不上，或watcher机制失效
	 */
	public void readFromMysql() {
		try {
			final ParamConfigDao dao = new ParamConfigDao();
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getType().getCanonicalName().equals(ZK_PARAM_NAME)) {
					String filedName = field.getName();
					Object zkParamInst = field.get(this);
					String path0 = (String) getZkPathMethod.invoke(zkParamInst);
					int logicid = (int) getIdMethod.invoke(zkParamInst);
					ParamConfig param = dao.getByLogicId(logicid);
					if (param != null) {
						String path1 = param.getZkpath();
						String path = (path1 != null && path1.length() > 0) ? path1 : path0;
						ZkParam newArgument = new ZkParam(param.getValue(), path, logicid);
						Method method = this.getClass().getMethod(ClassUtil.parseSetName(filedName), ZkParam.class);
						method.invoke(this, newArgument);
						logger.info("set " + filedName + " to " + newArgument.getValue());
					} else {
						logger.error("have no such param whoese logicid is " + logicid + " im mysql");
					}
				}
			}
		} catch (Exception e) {
			logger.error("read param from mysql failed", e);
			SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
					SystemConfig.getValue("mail_receiver"), "read param from mysql failed<br>" + e.getMessage());
		}
		addListener();
	}

	/**
	 * 把参数写入到zookeeper
	 * 
	 * 
	 */
	public void flushToZookeeper() {
		try {
			final ParamConfigDao dao = new ParamConfigDao();
			CuratorFramework zkClient = ZkClient.getInstance().getZkClient();
			CreateBuilder cb = zkClient.create();
			Field[] fields = this.getClass().getDeclaredFields();
			for (final Field field : fields) {
				field.setAccessible(true);
				if (field.getType().getCanonicalName().equals(ZK_PARAM_NAME)) {
					Object zkParamInst = field.get(this);
					String path = (String) getZkPathMethod.invoke(zkParamInst);
					// 如果对应的zk节点不存在
					if (zkClient.checkExists().forPath(path) == null) {
						// 则创建对应的zk节点
						cb.creatingParentsIfNeeded().forPath(path, new byte[] { 0 });
						logger.info("create zookeeper path " + path);
					}
					// 默认值是类中给的值
					double value = (double) getValueMethod.invoke(zkParamInst);
					int logicid = (int) getIdMethod.invoke(zkParamInst);
					ParamConfig param = dao.getByLogicId(logicid);
					if (param != null) {
						// 如果mysql中相应的参数值，则以mysql中的为准
						value = param.getValue();
					}
					// 给zk节点赋值
					zkClient.setData().forPath(path, String.valueOf(value).getBytes());
					logger.info("write " + value + " to zookeeper path " + path);
				}
			}
		} catch (Exception e) {
			logger.error("flush parameter to zookeeper failed", e);
			SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
					SystemConfig.getValue("mail_receiver"), "flush parameter to zookeeper failed<br>" + e.getMessage());
		}
	}
}
