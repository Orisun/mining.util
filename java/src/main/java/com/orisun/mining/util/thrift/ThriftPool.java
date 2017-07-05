package com.orisun.mining.util.thrift;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.thrift.protocol.TProtocol;

import com.orisun.mining.util.Pair;

public class ThriftPool {
	private static Log logger = LogFactory.getLog(ThriftPool.class);
	// 可用的服务器列表
	private CopyOnWriteArrayList<ObjectPool<TProtocol>> availableServer = new CopyOnWriteArrayList<ObjectPool<TProtocol>>();
	// 存活检测的最长间隔时间为10分钟
	private final int MAX_SLEEP = 1000 * 60 * 10;
	private final int MIM_SLEEP = 100;
	// 休息的时间间隔加上一个随机数
	private final int RADOM_SLEEP = 30;
	private Random random = new Random();

	public void addPool(ObjectPool<TProtocol> pool) {
		availableServer.add(pool);
	}

	public Pair<ObjectPool<TProtocol>, TProtocol> getPoolAndProtocol() {
		if (availableServer.size() == 0) {
			logger.fatal("no available thrift server");
			return null;
		}

		int idx = random.nextInt(availableServer.size());// 随机选择一台服务器，负载均衡
		ObjectPool<TProtocol> pool = null;
		try {
			pool = availableServer.get(idx);
		} catch (Exception e) {
			logger.error("can not get one thrift server", e);
			return null;
		}

		TProtocol protocol = null;
		try {
			protocol = pool.borrowObject();
			return Pair.of(pool, protocol);
		} catch (Exception e) {
			logger.error("get thrift server failed", e);
			/**
			 * 发生异常时很可能是该server上的连接池达到上限了，也可能是server挂了<br>
			 * 先把server从容器中移除，再开启异步线程不停检测其是否可用，
			 * 如果可用则再放回到容器中
			 */
			availableServer.remove(pool);
			checkAlive(pool, MIM_SLEEP);
			return getPoolAndProtocol();// 失败则递归调用
		}
	}

	private void checkAlive(final ObjectPool<TProtocol> pool, final int sleppTime) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					pool.borrowObject();
					availableServer.add(pool);// 复活，则放回容器
				} catch (Exception e) {
					int sleepTime = Math.min((int) (sleppTime * Math.sqrt(2)), MAX_SLEEP);
					if (sleepTime >= MAX_SLEEP) {
						logger.fatal("one thrift server have died for " + MAX_SLEEP + " milliseconds at least");
					}
					checkAlive(pool, sleepTime);// 仍然是死亡状态，则递归调用checkAlive，不过这次休息的时间更长
				}
			}
		}, sleppTime + random.nextInt(RADOM_SLEEP));
	}
}
