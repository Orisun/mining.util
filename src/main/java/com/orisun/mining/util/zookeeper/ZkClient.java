package com.orisun.mining.util.zookeeper;

import com.orisun.mining.util.NIC;
import com.orisun.mining.util.Release;
import com.orisun.mining.util.SystemConfig;
import com.orisun.mining.util.monitor.SendMail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

/**
 * 利用zookeeper进行参数配置
 * 
 * @Author:orisun
 * @Since:2016-3-22
 * @Version:1.0
 */
public class ZkClient extends Release {

	private static Log logger = LogFactory.getLog(ZkClient.class);
	private static String basePath = null;
	private static CuratorFramework zkClient = null;
	private static volatile ZkClient instance = null;

	public String getBasePath() {
		return basePath;
	}

	/**
	 * 若要使用ZkClient，必须在SystemConfig配置文件中指定zookeeper_base_path
	 * 
	 * @return
	 */
	public static ZkClient getInstance() {
		if (!isConnected()) {
			logger.info("zookeeper connection is lost, and will reconnect");
			instance = null;
		}
		if (instance == null) {
			synchronized (ZkClient.class) {
				if (instance == null) {
					String zpath = SystemConfig.getValue("zookeeper_base_path");
					// 线上机器和线下机器的zk basepath用不同的路径
					if (!NIC.onlineMachine()) {
						zpath += "_offline";
					}
					logger.debug("base zk path is " + zpath);
					if (zpath != null) {
						basePath = zpath;
						instance = new ZkClient();
					} else {
						logger.fatal("zookeeper_base_path is not set in system configuration");
						SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
								SystemConfig.getValue("mail_receiver"),
								"zookeeper_base_path is not set in system configuration");
						System.exit(0);
					}
				}
			}
		}
		return instance;
	}

	/**
	 * 执行一个checkExists操作，判断zookeeper连接是否正常
	 * 
	 * @return
	 */
	private static boolean isConnected() {
		boolean rect = false;
		try {
			if (zkClient.checkExists().forPath(basePath) != null) {
				rect = true;
			}
		} catch (Exception e) {

		}
		return rect;
	}

	private ZkClient() {
		zkClient = CuratorFrameworkFactory.builder().connectString(SystemConfig.getValue("zookeeper"))
				.sessionTimeoutMs(30000).connectionTimeoutMs(30000).retryPolicy(new ExponentialBackoffRetry(1000, 10))
				.defaultData(null).build();
		zkClient.start();
		logger.info("connect to zookeeper");
		CreateBuilder cb = zkClient.create();
		try {
			if (zkClient.checkExists().forPath(basePath) == null) {
				cb.creatingParentsIfNeeded().forPath(basePath, new byte[] { 0 });
			}
		} catch (Exception e) {
			logger.fatal("create zookeeper base path " + basePath + " failed", e);
			SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
					SystemConfig.getValue("mail_receiver"),
					"create zookeeper base path " + basePath + " failed<br>" + e.getMessage());
			System.exit(1);
		}
	}

	public CuratorFramework getZkClient() {
		return zkClient;
	}

	@Override
	public void releaseResource() {
		CloseableUtils.closeQuietly(zkClient);
		logger.info("close zookeeper connection quietly");
	}

}
