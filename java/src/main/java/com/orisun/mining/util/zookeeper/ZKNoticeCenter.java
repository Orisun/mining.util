package com.orisun.mining.util.zookeeper;

import com.orisun.mining.util.Release;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.KeeperException;

/**
 * 通过zookeeper节点监听机制，实现分布式消息通知
 * 
 * @Author:orisun
 * @Since:2015-9-25
 * @Version:1.0
 */
public class ZKNoticeCenter extends Release {

	private static Log logger = LogFactory.getLog(ZKNoticeCenter.class);

	private CuratorFramework curatorFramework;
	private String znotePath; // 待更新的zookeeper节点
	private NodeCache nodecache;

	public ZKNoticeCenter(String zkConnStr, String znodePath) {
		this.curatorFramework = CuratorFrameworkFactory.builder().connectString(zkConnStr).sessionTimeoutMs(5000)
				.connectionTimeoutMs(3000).retryPolicy(new ExponentialBackoffRetry(1000 * 60, 3))// 每隔1分钟发起一次连接请求，每次请求最多重试3次，如果3次都连不上则报异常
				.build();
		this.znotePath = znodePath;
		try {
			nodecache = new NodeCache(this.curatorFramework, this.znotePath);
			nodecache.start();
			addShutdownHook();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		addShutdownHook();
	}

	/**
	 * 更新zk某个节点上的值，如果节点不存在则先创建。其他进程如果在监听这个节点则能收到通知
	 * 
	 * @param nodeValue
	 *            更新后的值
	 * @throws Exception
	 */
	public void notice(String nodeValue) throws Exception {
		byte[] bytes = nodeValue.getBytes();
		try {
			curatorFramework.setData().forPath(znotePath, bytes);
		} catch (KeeperException.NoNodeException e) {
			curatorFramework.create().creatingParentsIfNeeded().forPath(znotePath, bytes);
		}
	}

	@Override
	public void releaseResource() {
		CloseableUtils.closeQuietly(nodecache);
	}

}
