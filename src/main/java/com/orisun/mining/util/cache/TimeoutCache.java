package com.orisun.mining.util.cache;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.serializer.JavaSerializer;

/**
 * 可以为每个元素设置存活时间的缓存容器，放在cache里的实体必须实现Serializable接口。
 * 
 * @Author orisun, Fido
 * @Since 2015-10-9
 * @Version 1.1
 */
public class TimeoutCache<K, V> {

	private static final Log logger = LogFactory.getLog(TimeoutCache.class);
	private ConcurrentMap<K, V> cacheObjMap = new ConcurrentHashMap<K, V>();
	private DelayQueue<DelayItem<Pair<K, V>>> queue = new DelayQueue<DelayItem<Pair<K, V>>>();
	private Thread daemonThread;

	public TimeoutCache() {
		Runnable daemonTask = new Runnable() {
			public void run() {
				daemonCheck();
			}
		};
		daemonThread = new Thread(daemonTask);
		daemonThread.setDaemon(true);
		daemonThread.setName("TimeoutCache Daemon Check");
		daemonThread.start(); // 启动后台线程，对容器中的元素不停地进行轮循，将过期的元素移除出出去
	}

	private void daemonCheck() {
		logger.info("check timeout element of cache started");
		for (;;) {
			try {
				DelayItem<Pair<K, V>> delayItem = queue.take();// 如果所有元素都没有超时，该行代码会阻塞
				if (delayItem != null) {
					Pair<K, V> pair = delayItem.getItem();
					cacheObjMap.remove(pair.first, pair.second); // 超时对象，从容器中移除
				}
			} catch (InterruptedException e) {
				logger.fatal("take timeout element from cache interrupted!", e);
				break; // 检测到中断时就退出循环
			}
		}
		logger.info("check timeout element of cache stopped.");
	}

	/**
	 * 以覆盖的方式向缓存中添加对象,缓存以<key,value>的形式存在.<br>
	 * 注意：value如果是List，则它不是由通过List.subList()得来的
	 * 。因为List.subList()返回的是一个RandomAccessSubList实例
	 * ,在反序列化时ObjectOutputStream.writeObject(RandomAccessSubList)会出错
	 * 
	 * @param key
	 * @param value
	 *            必须实现Serializable
	 * @param time
	 *            对象在缓存中的生存时间
	 * @param unit
	 *            时间单位
	 * @return 如果key在缓存中存在则返回旧的key值，否则返回null
	 */
	public V put(K key, V value, long time, TimeUnit unit) {
		V oldValue = cacheObjMap.put(key, value);
		if (oldValue != null)
			queue.remove(key);

		long nanoTime = TimeUnit.NANOSECONDS.convert(time, unit);
		queue.put(new DelayItem<Pair<K, V>>(new Pair<K, V>(key, value), nanoTime));
		return oldValue;
	}

	/**
	 * 默认缓存生存时间为10分钟的put方法，兼容Cache基类的方式
	 *
	 * @param key
	 * @param value
	 *            必须实现Serializable
	 * @return 如果key在缓存中存在则返回旧的key值，否则返回null
	 * @since 1.1
	 */
	public V put(K key, V value) {
		return put(key, value, 10, TimeUnit.MINUTES);
	}

	/**
	 * 清空缓存
	 */
	public void clear() {
		cacheObjMap.clear();
		queue.clear();
	}

	/**
	 * 根据key从缓存中取得对应的value,如果key不存在则返回null<br>
	 * 取出的是value的深拷贝 下一版本将在key不存在时默认报Exception,如需要默认返回null,请调用getIfPresent
	 * #Cache接口统一计划
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V get(K key) {
		try {
			V value = (V) JavaSerializer.deepCopy(cacheObjMap.get(key));
			return value;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
