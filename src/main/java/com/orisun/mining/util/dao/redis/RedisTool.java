package com.orisun.mining.util.dao.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.LinkedList;
import java.util.List;

/**
 * 在一次程序调用(一次 HTTP 请求或者一次 dubbo 调用)中，因为网络时延，应尽量少与 Redis交互:<br>
 * <ul>
 * <li>多次 get 可以用 mget，多次 set 可以用 mset
 * <li>set value 之后再 expire 可以用 setex 或者 psetex
 * <li>Hash: 多次 hget 可以用 hmget，多次 hset 可以用 hmset，hdel 可以同时删除多个
 * <li>Sorted Set: zadd 可以同时添加多个，zrem 可以同时移除多个
 * <li>Set: sadd 可以同时添加多个，srem 可以同时移除多个
 * </ul>
 * 尽可能使用 hash/list/sorted set/set 等数据结构，Redis 能这些数据进行压缩，从而节省内存
 * 
 * @author orisun
 * @date 2017年5月3日
 */
public class RedisTool {

	private static final Log logger = LogFactory.getLog(RedisTool.class);
	private static final byte[] REDIS_NULL = new byte[] { 'n', 'i', 'l' };
	private static final String REDIS_NULL_STRING = "nil";

	/**
	 * redis中的null值有特殊的表示方式
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isRedisNull(byte[] value) {
		if (value == null) {
			return true;
		}
		if (value.length == 3) {
			if (value[0] == REDIS_NULL[0] && value[1] == REDIS_NULL[1] && value[2] == REDIS_NULL[2]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回一个string型的value是否为null
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isRedisNull(String value) {
		return REDIS_NULL_STRING.equals(value);
	}

	/**
	 * 分页取出一个长list中的所有元素。<br>
	 * 虽然每次取一页{@code lrange(key,start,end)}
	 * 的时间复杂度为O(end)--跟MySQL分页查询一样，不论读取第几页，每次总是从第0个位置开始读。<br>
	 * 分页读的好处是网络传输的数据量小，坏处是增加了对redis的调用次数<br>
	 * 如果集合不是太大，直接调用{@code List<byte[]> result=jedisClient.lrange(key,0,-1)}
	 * 效率会更高
	 * 
	 * @param redisClient
	 * @param key
	 * @return
	 */
	public static List<byte[]> traverseLongList(Jedis redisClient, byte[] key) {
		List<byte[]> values = new LinkedList<byte[]>();
		try {
			Long len = redisClient.llen(key);// 时间复杂度为O(1)
			if (len > 0) {
				final int batch = 10000;
				int i = 0;
				for (; i < len / batch; i++) {
					List<byte[]> subList = redisClient.lrange(key, i * batch, (1 + i) * batch - 1);
					values.addAll(subList);
					try {
						Thread.sleep(20);// 休息20ms，避免redis压力太大
					} catch (InterruptedException e) {

					}
				}
				List<byte[]> subList = redisClient.lrange(key, i * batch, len - 1);
				values.addAll(subList);
			}
		} catch (Exception e) {
			logger.error("traverse redis list failed", e);
		}
		return values;
	}

	/**
	 * redis是单线程的，删除超长list时会明显阻塞
	 * 
	 * {@see 如何优雅地删除Redis大键
	 * http://blog.csdn.net/wsliangjian/article/details/52329320}
	 * 
	 * @param redisClient
	 * @param key
	 */
	public static void deleteLongList(Jedis redisClient, byte[] key) {
		while (redisClient.llen(key) > 0) {
			redisClient.ltrim(key, 100, -1);
		}
	}

	/**
	 * 由于redis是单线程的，操作一个大集合时会阻塞很长时间，所以要分页取一个大set里的元素。<br>
	 * 如果set不是很大，直接调用{@code Set<byte[]> result = jedisClient.smembers(key)}
	 * 效率会更高。
	 * 
	 * @param redisClient
	 * @param key
	 * @return
	 */
	public static List<byte[]> traverseBigSet(Jedis redisClient, byte[] key) {
		List<byte[]> values = new LinkedList<byte[]>();
		try {
			Long len = redisClient.scard(key);
			if (len > 0) {
				// System.out.println("len = " + len);
				final int batch = 10000;
				ScanParams params = new ScanParams();
				params.count(batch);
				byte[] cursor = "0".getBytes();
				while (true) {
					ScanResult<byte[]> result = redisClient.sscan(key, cursor, params);
					List<byte[]> datas = result.getResult();
					values.addAll(datas);
					cursor = result.getCursorAsBytes();
					if ("0".equals(new String(cursor))) {
						break;
					}
					try {
						Thread.sleep(20);// 休息20ms，避免redis压力太大
					} catch (InterruptedException e) {

					}
				}
			}
		} catch (Exception e) {
			logger.error("traverse redis set failed", e);
		}
		return values;
	}

	/**
	 * redis是单线程的，删除超长list时会明显阻塞
	 * 
	 * {@see 如何优雅地删除Redis大键
	 * http://blog.csdn.net/wsliangjian/article/details/52329320}
	 * 
	 * @param redisClient
	 * @param key
	 */
	public static void deleteBigSet(Jedis redisClient, byte[] key) {
		try {
			Long len = redisClient.scard(key);
			if (len > 0) {
				// System.out.println("len = " + len);
				final int batch = 500;
				ScanParams params = new ScanParams();
				params.count(batch);
				byte[] cursor = "0".getBytes();
				while (true) {
					ScanResult<byte[]> result = redisClient.sscan(key, cursor, params);
					List<byte[]> datas = result.getResult();
					for (byte[] ele : datas) {
						redisClient.srem(key, ele);
					}
					cursor = result.getCursorAsBytes();
					if ("0".equals(new String(cursor))) {
						break;
					}
					try {
						Thread.sleep(20);// 休息20ms，避免redis压力太大
					} catch (InterruptedException e) {

					}
				}
			}
		} catch (Exception e) {
			logger.error("traverse redis set failed", e);
		}
	}
}
