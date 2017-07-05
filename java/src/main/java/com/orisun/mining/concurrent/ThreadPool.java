package com.orisun.mining.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * 
 * @Author:orisun
 * @Since:2016-3-24
 * @Version:1.0
 */
public class ThreadPool {

	private static Log logger = LogFactory.getLog(ThreadPool.class);
	private LinkedBlockingQueue<Runnable> queue = null;
	private ThreadPoolExecutor executors = null;

	/**
	 * 
	 * @param threadNum
	 *            线程池大小
	 * @param lengthOfWaitQueue
	 *            任务等待队列的长度，如果等待队列已满则新的任务请求会被丢弃
	 */
	public ThreadPool(int threadNum, int lengthOfWaitQueue) {
		queue = new LinkedBlockingQueue<Runnable>(lengthOfWaitQueue);// 普通Collection在构造函数里指定capacity后不断添加元素容器会自动扩容，但是LinkedBlockingQueue的size达到capacity后不会自动扩容而是会抛出异常
		executors = new ThreadPoolExecutor(threadNum, // 线程池中的最小线程数
				threadNum, // 线程池中的最大线程数
				0L, // 当线程池中有空闲的线程（指多出最小线程数的那部分空闲线程）时，经过多长时间终止这些空闲线程
				TimeUnit.MILLISECONDS, // 终止空闲线程时间间隔的单位
				queue, // 存入执行任务的队列
				/**
				 * 线程池对拒绝任务的处理策略，有四个选择如下：
				 * ThreadPoolExecutor.AbortPolicy()：抛出java.util
				 * .concurrent.RejectedExecutionException异常
				 * ThreadPoolExecutor.CallerRunsPolicy
				 * ()：重试添加当前的任务，他会自动重复调用execute()方法
				 * ThreadPoolExecutor.DiscardOldestPolicy()：抛弃旧的任务
				 * ThreadPoolExecutor.DiscardPolicy()：抛弃当前的任务
				 */
				new ThreadPoolExecutor.AbortPolicy());
	}

	/**
	 * 往线程池中添加任务。等待队列满时，请求就被阻塞
	 * 
	 * @param run
	 */
	public void addTask(Runnable run) {
		while (getRemainingCapacity() <= 0) {
			logger.debug("thread pool waiting queue is full.size=" + getSize());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		executors.execute(run);
	}

	/**
	 * 往线程池中添加任务。等待队列满时，请求就被阻塞
	 * 
	 * @param callable
	 */
	public void addTask(Callable<?> callable) {
		while (getRemainingCapacity() <= 0) {
			logger.error("thread pool waiting queue is full.size=" + getSize());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		executors.submit(callable);
	}

	public int getRemainingCapacity() {
		return queue.remainingCapacity();
	}

	public int getSize() {
		return queue.size();
	}

	/**
	 * 设置超时时间，等待线程池中的所有任务执行完毕，最后关闭线程池
	 * 
	 * @param timeout
	 * @param unit
	 * @throws Exception
	 *             如果达到了超时时间还有任务没结束，则抛出异常
	 */
	public void waitAllTaskFinished(long timeout, TimeUnit unit) throws Exception {
		executors.shutdown();// 停止接收新的请求，并继续执行还没有完成的任务
		if (!executors.awaitTermination(timeout, unit)) {// 阻塞，直到线程池中的所有任务都结束或者超时。如果到了超时时间线程池中还有任务未结束，则返回false
			executors.shutdownNow();// 立即强行结束
			logger.error("some task in the thread pool do not finish in time!");
		}
	}
}
