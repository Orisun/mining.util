package com.orisun.mining.util.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.ObjectPool;
import org.apache.thrift.protocol.TProtocol;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.Path;

public class TestRecSortThrift {

	private static String basePath = null;
	private static String confPath = null;
	private static Feature feature = new Feature();
	private static List<Feature> features = new ArrayList<Feature>();

	@BeforeClass
	public static void setup() {
		basePath = Path.getCurrentPath();
		confPath = basePath + "/config";
		ThriftPoolManager.configThriftPool(ThriftServerName.POS_REC_SORT, confPath + "/thrift_recsort.properties");
		String[] brr = "20.0    30.0    80.0    82.0    1.0     1.0     0.832704244917  1.0     0.521220749493  1.0     0.818875758452  0.5     1000.0  52172.0 52.172  16.695  30.06   5.0     1.300 0.0   0.683254955817  0.289581275389  0.639441954436  0.0     0.302382928629  0.0     0.0"
				.split("\\s+");
		List<Double> list = new ArrayList<Double>();
		for (String ele : brr) {
			list.add(Double.parseDouble(ele));
		}

		feature.setArr(list);
		for (int i = 0; i < 5000; i++) {
			features.add(feature);
		}
	}

	@Test
	public void testNormal() {
		Pair<ObjectPool<TProtocol>, TProtocol> poolAndProtocol = ThriftPoolManager
				.getThriftPool(ThriftServerName.POS_REC_SORT).getPoolAndProtocol();
		if (poolAndProtocol == null) {
			Assert.assertTrue(false);
		}
		ObjectPool<TProtocol> thriftPool = poolAndProtocol.first;
		TProtocol protocol = poolAndProtocol.second;
		try {
			RecSort.Client client = new RecSort.Client(protocol);
			long t1 = System.currentTimeMillis();
			double score = client.getScore(feature);
			long t2 = System.currentTimeMillis();
			System.out.println("get score:" + score + ", use time " + (t2 - t1));
			t2 = System.currentTimeMillis();
			List<Double> scores = client.batchGetScore(features);
			long t3 = System.currentTimeMillis();
			System.out.println("batch use time " + (t3 - t2));
			for (Double ele : scores) {
				Assert.assertTrue(ele == score);
			}
			thriftPool.returnObject(protocol);// 正常情况下，用完后一定要归还
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	/**
	 * 只借不还
	 */
	@Test
	public void testExhausted() {
		List<Pair<ObjectPool<TProtocol>, TProtocol>> pools = new ArrayList<Pair<ObjectPool<TProtocol>, TProtocol>>();
		try {
			// 配置文件里面指定的是最多10个连接
			for (int i = 0; i < 10; i++) {
				Pair<ObjectPool<TProtocol>, TProtocol> poolAndProtocol = ThriftPoolManager
						.getThriftPool(ThriftServerName.POS_REC_SORT).getPoolAndProtocol();
				if (poolAndProtocol == null) {
					Assert.assertTrue(false);
				}
				pools.add(poolAndProtocol);
				TProtocol protocol = poolAndProtocol.second;
				RecSort.Client client = new RecSort.Client(protocol);
				double score = client.getScore(feature);
				System.out.println("i=" + i + ", get score:" + score);
				// 不还
			}
			System.out.println("OK");
			// 由于只有一台server，且该server上的连接池已达上限，所以返回null
			Pair<ObjectPool<TProtocol>, TProtocol> poolAndProtocol = ThriftPoolManager
					.getThriftPool(ThriftServerName.POS_REC_SORT).getPoolAndProtocol();
			for (Pair<ObjectPool<TProtocol>, TProtocol> ele : pools) {
				ele.first.returnObject(ele.second);// 归还
			}
			if (poolAndProtocol == null) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	/**
	 * 批量测试单算接口<br>
	 * 平均每次调用耗时8.8ms
	 */
	@Test
	public void batchTest1() {
		try {
			long begin = System.currentTimeMillis();
			int loop = 10000;
			for (int i = 0; i < loop; i++) {
				Pair<ObjectPool<TProtocol>, TProtocol> poolAndProtocol = ThriftPoolManager
						.getThriftPool(ThriftServerName.POS_REC_SORT).getPoolAndProtocol();
				if (poolAndProtocol == null) {
					Assert.assertTrue(false);
				}
				ObjectPool<TProtocol> thriftPool = poolAndProtocol.first;
				TProtocol protocol = poolAndProtocol.second;
				RecSort.Client client = new RecSort.Client(protocol);
				client.getScore(feature);
				thriftPool.returnObject(protocol);
			}
			long end = System.currentTimeMillis();
			System.out.println("total call " + loop + " times, average use time " + 1.0 * (end - begin) / loop);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	/**
	 * 批量测试批算接口<br>
	 * 平均每次调用耗时627ms，所以当批量为5000条时，批算接口比单算接口快70倍
	 */
	@Test
	public void batchTest2() {
		try {
			// 配置文件里面指定的是最多10个连接
			long begin = System.currentTimeMillis();
			int loop = 100;
			for (int i = 0; i < loop; i++) {
				Pair<ObjectPool<TProtocol>, TProtocol> poolAndProtocol = ThriftPoolManager
						.getThriftPool(ThriftServerName.POS_REC_SORT).getPoolAndProtocol();
				if (poolAndProtocol == null) {
					Assert.assertTrue(false);
				}
				ObjectPool<TProtocol> thriftPool = poolAndProtocol.first;
				TProtocol protocol = poolAndProtocol.second;
				RecSort.Client client = new RecSort.Client(protocol);
				client.batchGetScore(features);
				thriftPool.returnObject(protocol);
			}
			long end = System.currentTimeMillis();
			System.out.println("total call " + loop + " times, average use time " + 1.0 * (end - begin) / loop);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
