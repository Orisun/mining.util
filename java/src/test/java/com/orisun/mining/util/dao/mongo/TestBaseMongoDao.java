package com.orisun.mining.util.dao.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import com.orisun.mining.util.Path;
import com.orisun.mining.util.SystemConfig;

public class TestBaseMongoDao {

	private static String basePath = null;
	private static String confPath = null;

	public static void main(String[] args) {
		basePath = Path.getCurrentPath();
		confPath = basePath + "/config/";
		try {
			SystemConfig.init(confPath + "system.properties");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		PropertyConfigurator.configure(confPath + "log4j.properties");

		MongoClientManager.configMongo(MongoDBName.TALENT_REC, confPath + "mongo_test.properties");
		MongoClientManager.configMongo(MongoDBName.POSITION_REC, confPath + "mongo_test.properties");

		int userid = 585858;
		UserModelDao userModelDao = new UserModelDao();
		UserModel userModel = new UserModel();
		Map<String, Double> city = new HashMap<String, Double>();
		city.put("北京", 0.8);
		city.put("上.海", 0.2);
		city.put(null, 0.2);
		Map<Integer, Double> cate = new HashMap<Integer, Double>();
		cate.put(15, 0.58);
		Map<EduDegree, Double> edu = new HashMap<EduDegree, Double>();
		edu.put(EduDegree.MASTER, 1.0);
		edu.put(null, 1.0);
		List<EduDegree> cates = new ArrayList<EduDegree>();
		// cates.add(EduDegree.MASTER);
		cates.add(null);
		cates.add(null);
		Set<EduDegree> bads = new HashSet<EduDegree>();
		bads.add(EduDegree.UNDERGRADUATE);
		bads.add(null);
		userModel.setUserid(userid);
		userModel.setCity(city);
		userModel.setEdu(edu);
		userModel.setCate(cates);
		userModel.setBad(bads);
		userModelDao.deleteByKey("userid", userid);
		userModelDao.insert(userModel);
		UserModel userModel2 = userModelDao.getByKey("userid", 585858).get(0);
		System.out.println("userid：");
		System.out.println(userModel2.getUserid());
		System.out.println("\nOK：");
		System.out.println(userModel2.getOk());
		System.out.println("\n城市：");
		for (Entry<String, Double> entry : userModel2.getCity().entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		System.out.println("\n学历：");
		for (Entry<EduDegree, Double> entry : userModel2.getEdu().entrySet()) {
			System.out.println(entry.getKey().getDesc() + "\t" + entry.getValue());
		}
		System.out.println("\n兴趣：");
		System.out.println(userModel2.getFavor());
		System.out.println("\n坏数据：");
		for (EduDegree ele : userModel2.getBad()) {
			System.out.println(ele.getDesc());
		}
		System.out.println("\n类别：");
		for (EduDegree ele : userModel2.getCate()) {
			System.out.println(ele.getDesc());
		}
		System.exit(0);

		int total = 0;
		int err = 0;
		while (total++ < 100) {
			List<UserNeighbor> neighbors = new ArrayList<UserNeighbor>();
			neighbors.add(new UserNeighbor(123, 58.8));
			neighbors.add(new UserNeighbor(465, 95.8));
			SimUser entity = new SimUser();
			entity.setUserid(159);
			entity.setNeighbors(neighbors);
			SimUserDao dao = new SimUserDao();
			dao.deleteByKey("userid", 159);// 删除
			dao.insert(entity);// 插入
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<SimUser> results = dao.getByKey("userid", 159);// 查找
			if (results != null && results.size() > 0) {
				System.out.println(results.get(0).getNeighbors().get(1).getUid());
			} else {
				err++;
				System.err.println("get none");
			}
			dao.deleteByKey("userid", 159);// 删除
		}
		System.out.println(1.0 * err / total);
	}
}
