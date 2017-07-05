package com.orisun.mining.util;

import com.orisun.mining.util.zookeeper.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Author:orisun
 * @Since:2016-4-7
 * @Version:1.0
 */
public class ServerCluster {

	private static Log logger = LogFactory.getLog(ServerCluster.class);
	private static final String BASE_PATH = ZkClient.getInstance().getBasePath() + "/cluster";
	private static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

	/**
	 * 向集群上报自己的存在，即把自己的IP写到特定的zk节点(EPHEMERAL节点)上去
	 */
	private static void reportServer() {
		String selfIP = NIC.getLocalIP();
		CuratorFramework zkClient = ZkClient.getInstance().getZkClient();
		boolean exists = false;
		try {
			CreateBuilder cb = zkClient.create();
			if (zkClient.checkExists().forPath(BASE_PATH) == null) {
				cb.creatingParentsIfNeeded().forPath(BASE_PATH, new byte[] { 0 });
			}
			List<String> children = zkClient.getChildren().forPath(BASE_PATH);
			if (children != null && children.indexOf(selfIP) >= 0) {
				exists = true;
			}
			if (!exists) {
				// EPHEMERAL节点，进程终止时zookeeper连接断开，节点自动被删除
				cb.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(BASE_PATH + "/" + selfIP,
						new byte[] { 0 });
				logger.info(selfIP + " add to cluster");
			} else {
				// 如果发现cluster上已存在该IP，则30秒后再确认一下
				logger.info(selfIP + " is already in cluster");
				Thread.sleep(1000 * 30);
				children = zkClient.getChildren().forPath(BASE_PATH);
				exists = false;
				if (children != null && children.indexOf(selfIP) >= 0) {
					exists = true;
				}
				if (!exists) {
					// EPHEMERAL节点，进程终止时zookeeper连接断开，节点自动被删除
					cb.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(BASE_PATH + "/" + selfIP,
							new byte[] { 0 });
					logger.info(selfIP + " add to cluster");
				}
			}
		} catch (Exception e) {
			logger.fatal("report to cluster failed", e);
		}
	}

	/**
	 * 向集群上报自己的存在，即把自己的IP写到特定的zk节点(EPHEMERAL节点)上去<br>
	 * 为防止zookeeper会话断开而造成节点被删除，每隔10分钟就去写一次
	 */
	public static void report() {
		exec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				reportServer();
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	/**
	 * 获取集群中有多少台机器
	 * 
	 * @return
	 */
	public static int getClusterSize() {
		int total = 0;
		List<String> children = null;
		try {
			CuratorFramework zkClient = ZkClient.getInstance().getZkClient();
			children = zkClient.getChildren().forPath(BASE_PATH);
		} catch (Exception e) {
			logger.error("get children of " + BASE_PATH + " failed", e);
		}
		if (children != null) {
			total = children.size();
		}
		logger.info("cluster size is " + total);
		return total;
	}

	/**
	 * 获取自己在集群中的编码(从0开始)
	 * 
	 * @return
	 */
	public static int getIndexInCluster() {
		int index = -1;
		CuratorFramework zkClient = ZkClient.getInstance().getZkClient();
		int tryTimes = 0;
		while (index < 0 && tryTimes++ < 3) {
			try {
				List<String> children = zkClient.getChildren().forPath(BASE_PATH);
				String selfIP = NIC.getLocalIP();
				index = children.indexOf(selfIP);
			} catch (Exception e) {
				logger.fatal("get cluster info failed", e);
			}
		}
		logger.info("this server's index is " + index);
		return index;
	}
}
