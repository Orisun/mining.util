package com.orisun.mining.util.dao;

import com.orisun.mining.util.Path;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBaseDao {

	private static HrScoreDao heScoreDao = null;
	private static ViewRecDao viewRecDao = null;
	private static HrFeatureDao flowFeatureDao = null;

	@BeforeClass
	public static void setup() {
		String basePath = Path.getCurrentPath();
		PropertyConfigurator.configure(basePath + "/config/log4j.properties");
		DaoHelperPool.configDb(DBName.RECOMMEND, basePath
				+ "/config/db_rec.properties", null);
		try {
			heScoreDao = new HrScoreDao();
			viewRecDao = new ViewRecDao();
			flowFeatureDao = new HrFeatureDao();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询并发测试。<br>
	 * 数据库配置：<br>
	 * <li>db_maxconn=30<br> <li>query_timeout=20<br> <li>refresh_interval=1<br>
	 * 压测情况： <li>10个线程<br> <li>每个线程查询10000次<br>
	 * 测试结果：<br> <li>平均每次查询2.27496毫秒<br> <li>成功率0.999814<br> <li>DB连接最高用到21个<br>
	 * 
	 * @throws Exception
	 */
	@Test
	public void concurrentTest1() throws Exception {
		final UserRecDao dao1 = new UserRecDao();
		final List<Integer> uidList = Arrays.asList(5388, 5390, 5421, 5429,
				5443, 5450, 5466, 5484, 5490, 5496, 5544, 5559, 5573, 5580,
				5587, 5603, 5610, 5624, 5631, 5637, 5644, 5650, 5652, 5654,
				5660, 5663, 5665, 5667, 5674, 5682, 5684, 5686, 5698, 5699,
				5709, 5739, 5742, 5750, 5756, 5767, 5768, 5773, 5778, 5791,
				5817, 5820, 5821, 5824, 5826, 5828, 5836, 5837, 5839, 5841,
				5855, 5859, 5863, 5890, 5892, 5898, 5901, 5906, 5913, 5925,
				5943, 5954, 5957, 5962, 5975, 5995, 6000, 6012, 6019, 6059,
				6064, 6077, 6098, 6100, 6102, 6108, 6110, 6114, 6115, 6123,
				6129, 6130, 6132, 6140, 6144, 6146, 6154, 6157, 6165, 6170,
				6180, 6207, 6208, 6229, 6242, 6244); // 这里的每个用户都有推荐
		final int threadNum = 5;// 开threadNum个线程并发测试
		final int ROUND = 10000;// 每个线程进行ROUND次接口调用
		final Random rnd = new Random();
		Thread[] threads = new Thread[threadNum];
		final AtomicInteger successCnt = new AtomicInteger(0); // 总成功次数
		for (int i = 0; i < threadNum; i++) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					int success = 0;// 本线程成功次数
					for (int round = 0; round < ROUND; round++) {
						int index = rnd.nextInt(uidList.size());
						int uid = uidList.get(index);
						try {
							List<UserRec> data = dao1.getDataByPage("rec_ids",
									"uid=" + uid, 1, 1);
							if (data.size() > 0) {
								success++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					successCnt.addAndGet(success);
				}
			};
			threads[i] = thread;
		}
		long begin = System.currentTimeMillis();
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("use " + 1.0 * (end - begin) / ROUND
				+ " miliseconds for each query");
		System.out.println("success ratio " + 1.0 * successCnt.get()
				/ (ROUND * threadNum));
	}

	/**
	 * 查询并发测试。<br>
	 * 数据库配置：<br>
	 * <li>db_maxconn=30<br> <li>query_timeout=20<br> <li>refresh_interval=1<br>
	 * 压测情况： <li>10个线程<br> <li>每个线程查询10000次<br>
	 * 测试结果：<br> <li>平均每次查询10.9496毫秒<br> <li>成功率0.92056<br> <li>DB连接最高用到26个<br>
	 * 
	 * @throws Exception
	 */
	@Test
	public void concurrentTest2() throws Exception {
		final DeliveryRecDao dao2 = new DeliveryRecDao();
		final List<Integer> pidList = Arrays.asList(149, 155, 309, 832, 921,
				945, 1039, 1073, 1076, 1079, 1098, 1146, 1318, 1319, 1328,
				1329, 1396, 1397, 1404, 1538, 1546, 1570, 1574, 1577, 1595,
				1631, 1633, 1743, 1746, 1772, 1774, 1824, 1826, 1828, 1835,
				1887, 1889, 1896, 1908, 2033, 2034, 2078, 2120, 2142, 2161,
				2166, 2168, 2295, 2319, 2323, 2336, 2503, 2518, 2619, 2651,
				2712, 2752, 2754, 2764, 2846, 2851, 2852, 2864, 2878, 2901,
				2902, 2968, 3043, 3047, 3051, 3061, 3065, 3157, 3159, 3222,
				3248, 3257, 3259, 3273, 3275, 3277, 3284, 3295, 3314, 3492,
				3494, 3496, 3538, 3596, 3618, 3737, 3755, 3829, 3941, 3981,
				4009, 4023, 4061, 4089, 4091);// 这里的每个职位都有推荐
		final int threadNum = 10;// 开threadNum个线程并发测试
		final int ROUND = 10000;// 每个线程进行ROUND次接口调用
		final Random rnd = new Random();
		Thread[] threads = new Thread[threadNum];
		final AtomicInteger successCnt = new AtomicInteger(0); // 总成功次数
		for (int i = 0; i < threadNum; i++) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					int success = 0;// 本线程成功次数
					for (int round = 0; round < ROUND; round++) {
						int index = rnd.nextInt(pidList.size());
						int pid = pidList.get(index);
						try {
							List<DeliveryRec> data = dao2.getDataByPage("*",
									"positionid=" + pid, 1, 100);
							if (data.size() > 0) {
								success++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					successCnt.addAndGet(success);
				}
			};
			threads[i] = thread;
		}
		long begin = System.currentTimeMillis();
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("use " + 1.0 * (end - begin) / ROUND
				+ " miliseconds for each query");
		System.out.println("success ratio " + 1.0 * successCnt.get()
				/ (ROUND * threadNum));
	}

	@Test
	public void testGetById() {
		int id = 3;
		HrScore inst = heScoreDao.getById(id);
		Assert.assertEquals(id, inst.getId());
		System.out.println(inst.getHrid());
		System.out.println(inst.getTotal());
		System.out.println(inst.getUpdatetime());
	}
	
	@Test
	public void testGetDateTime() throws Exception {
		int id = 1;
		UserDeliverDao dao=new UserDeliverDao();
		UserDeliver inst=dao.getById(id);
		Date d=inst.getDeliverTime();
		System.out.println(d);
	}

	@Test
	public void testGetDataByPage() throws Exception {
		UserRecDao dao1 = new UserRecDao();
		int userId = 28836;
		List<UserRec> list1 = dao1.getDataByPage("*", "uid=" + userId, 1, 1);
		for (UserRec vo : list1) {
			System.out.println(vo.getRecids());
		}

		List<HrScore> list = heScoreDao.getDataByPage("id",
				"updatetime>20150921154000 and updatetime<20150921155000", 5,
				10);
		for (HrScore inst : list) {
			Assert.assertTrue(0.0 == inst.getTotal());
			System.out.println(inst.getId());
		}

		List<ViewRec> datas = viewRecDao.getDataByPage(
				"recpositionid,recscore", "positionid=11", 1, 1000);
		for (ViewRec inst : datas) {
			System.out.println(inst.getRecpositionid() + "\t"
					+ inst.getRecscore());
		}
	}

	@Test
	public void testInsert() {
		HrScore inst = new HrScore();
		inst.setHrid(20151106);
		inst.setTotal(423.654);
		inst.setUpdatetime(new Timestamp(63756756));
		heScoreDao.insert(inst);

		HrScore inst2 = new HrScore();
		heScoreDao.insert(inst2);

		HrFlowFeature inst3 = new HrFlowFeature();
		inst3.setHrid(1);
		inst3.setToDeal(5);
		flowFeatureDao.insert(inst3);
	}

	@Test
	public void testBatchInsert() {
		List<HrScore> datas = new ArrayList<HrScore>();
		HrScore inst3 = new HrScore();
		inst3.setHrid(2345);
		HrScore inst4 = new HrScore();
		inst4.setHrid(5432);
		datas.add(inst3);
		datas.add(inst4);
		heScoreDao.batchInsert(datas);

		List<HrFlowFeature> datas2 = new ArrayList<HrFlowFeature>();
		HrFlowFeature inst5 = new HrFlowFeature();
		inst5.setHrid(2);
		inst5.setToDeal(55);
		HrFlowFeature inst6 = new HrFlowFeature();
		inst6.setHrid(3);
		inst6.setToDeal(85);
		datas2.add(inst5);
		datas2.add(inst6);
		flowFeatureDao.batchInsert(datas2);
	}

	@Test
	public void testUpdate() {
		HrScore inst = heScoreDao.getDataByPage("*", "hrid=20151106", 1, 1)
				.get(0);
		double total = inst.getTotal();
		System.out.println("原total:" + total);
		inst.setTotal(10 + total);
		System.out.println("影响" + heScoreDao.update(inst) + "条数据");
		inst = heScoreDao.getDataByPage("*", "hrid=20151106", 1, 1).get(0);
		total = inst.getTotal();
		System.out.println("更新后total:" + total);

		HrFlowFeature inst2 = flowFeatureDao.getDataByPage("*", "hrid=2", 1, 1)
				.get(0);
		int toDeal = inst2.getToDeal();
		System.out.println("原toDeal:" + toDeal);
		inst2.setToDeal(toDeal + 15);
		System.out.println("影响" + flowFeatureDao.update(inst2) + "条数据");
		inst2 = flowFeatureDao.getDataByPage("*", "hrid=2", 1, 1).get(0);
		toDeal = inst2.getToDeal();
		System.out.println("更新后toDeal:" + toDeal);
	}

}
