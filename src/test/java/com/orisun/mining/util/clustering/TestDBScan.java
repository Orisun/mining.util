package com.orisun.mining.util.clustering;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.Path;
import com.orisun.mining.util.WordSegHandler;
import com.orisun.mining.util.filter.TextProcess;
import com.orisun.mining.util.sort.MapSorter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class TestDBScan {

	private static String basePath = null;
	private static Set<Character> validPunc = new HashSet<Character>();
	private static DecimalFormat df = new DecimalFormat("#.###");
	private static final double SIM_THRESH = 0.4;
	private static final int MIN_PTS = 2;

	@BeforeClass
	public static void setup() throws IOException {
		basePath = Path.getCurrentPath();
		Idf.init(basePath + "/data/idf.dict");
		WordSegHandler.importUserDict(basePath + "/data/it.dic");
		validPunc.add('+');
		validPunc.add('.');
		validPunc.add('#');
		validPunc.add('-');
		validPunc.add('/');
	}

	@Test
	public void testCase6() throws IOException {
		System.out.println("=============userid:1979900===============");
		clusterFromFile(basePath + "/data/user_deliver/1979900");
	}

	@Test
	public void testCase7() throws IOException {
		System.out.println("=============userid:2979897===============");
		clusterFromFile(basePath + "/data/user_deliver/2979897");
	}

	@Test
	public void testCase8() throws IOException {
		System.out.println("=============userid:3079907===============");
		clusterFromFile(basePath + "/data/user_deliver/3079907");
	}

	@Test
	public void testCase9() throws IOException {
		System.out.println("=============userid:144561===============");
		clusterFromFile(basePath + "/data/user_deliver/144561");
	}

	private void clusterFromFile(String file) throws IOException {
		List<UserInterest> actions = new ArrayList<UserInterest>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\\s+", 2);
			if (arr.length == 2) {
				actions.add(new UserInterest(arr[0], arr[1].trim()));
			}
		}
		br.close();
		List<ClusterObject<UserInterest>> interests = new ArrayList<ClusterObject<UserInterest>>();
		for (UserInterest action : actions) {
			interests.add(new ClusterObject<UserInterest>(action));
		}
		DBScan<UserInterest> dbscan = new DBScan<UserInterest>(1 - SIM_THRESH,
				MIN_PTS);
		dbscan.cluster(interests);// 聚类
		// 分到各自的簇里面去
		Map<Integer, List<ClusterObject<UserInterest>>> clusters = new HashMap<Integer, List<ClusterObject<UserInterest>>>();
		for (ClusterObject<UserInterest> ele : interests) {
			int cid = ele.getCid();
			// 如果是独立点，它自己就是簇心，直接回到rect里
			if (cid < 0) {
				System.out.println(cid + " : " + ele.getData().getPosition());
				System.out.println();
			} else {
				List<ClusterObject<UserInterest>> list = clusters.get(cid);
				if (list == null) {
					list = new ArrayList<ClusterObject<UserInterest>>();
				}
				list.add(ele);
				clusters.put(cid, list);
			}
		}
		for (Entry<Integer, List<ClusterObject<UserInterest>>> entry : clusters
				.entrySet()) {
			int cid = entry.getKey();
			Map<String, Double> termWeight = new HashMap<String, Double>();
			double totalWeight = 0.0;
			for (ClusterObject<UserInterest> ele : entry.getValue()) {
				System.out.println(cid + " : " + ele.getData().getPosition());
				for (Pair<String, Double> pair : ele.getData()
						.getPositionSegs()) {
					String term = pair.first;
					double weight = pair.second;
					Double oldWeight = termWeight.get(term);
					totalWeight += weight;
					if (oldWeight == null) {
						oldWeight = new Double(0);
					}
					termWeight.put(term, oldWeight + weight);
				}
			}
			List<Entry<String, Double>> sortedMap = MapSorter.sortMapByValue(
					termWeight, true);
			for (Entry<String, Double> ele : sortedMap) {
				System.out.print(ele.getKey() + ":"
						+ df.format(ele.getValue() / totalWeight) + ",");
			}
			System.out.println();
			System.out.println();
		}
	}

	class UserInterest implements ClustertInst {

		private String city;
		private String position;
		// 必须用List不能用Map，因为计算Jaccard时要求是有序的。pair.first是词，pair.second是词出现的次数权重
		private List<Pair<String, Double>> positionSegs;

		public UserInterest(String city, String cont) {
			this.city = city;
			this.position = cont;
			try {
				List<String> segs = WordSegHandler.wordSeg(TextProcess.rmPunc(
						cont, validPunc));
				int len = segs.size();
				if (len > 0) {
					Collections.sort(segs);
					List<Pair<String, Double>> segsWeight = new ArrayList<Pair<String, Double>>();
					for (String ele : segs) {
						segsWeight.add(Pair.of(ele, 1.0 / len));
					}
					this.positionSegs = segsWeight;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public List<Pair<String, Double>> getPositionSegs() {
			return positionSegs;
		}

		public void setPositionSegs(List<Pair<String, Double>> positionSegs) {
			this.positionSegs = positionSegs;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		@Override
		public double simWith(ClustertInst other) {
			double sim = 0.0;
			if (other instanceof UserInterest) {
				UserInterest another = (UserInterest) other;
				// 城市不一样，相似度为0
				if (this.getCity() != null && another.getCity() != null
						&& !this.getCity().equals(another.getCity())) {
					return 0.0;
				}
				if (this.getPositionSegs() != null
						&& this.getPositionSegs().size() > 0
						&& another.getPositionSegs() != null
						&& another.getPositionSegs().size() > 0) {
					int len1 = this.getPositionSegs().size();
					int len2 = another.getPositionSegs().size();
					int idx1 = 0;
					int idx2 = 0;
					double intersect = 0.0; // 交集
					double union = 0.0; // 并集
					while (idx1 < len1 && idx2 < len2) {
						String term1 = this.getPositionSegs().get(idx1).first;
						String term2 = another.getPositionSegs().get(idx2).first;
						if (term1.equals(term2)) {
							double weight = Idf.getIdf(term1);
							intersect += weight;
							union += weight;
							idx1++;
							idx2++;
						} else if (term1.compareTo(term2) < 0) {
							double weight = Idf.getIdf(term1);
							union += weight;
							idx1++;
						} else {
							double weight = Idf.getIdf(term2);
							union += weight;
							idx2++;
						}
					}
					while (idx1 < len1) {
						String term1 = this.getPositionSegs().get(idx1).first;
						double weight = Idf.getIdf(term1);
						union += weight;
						idx1++;
					}
					while (idx2 < len2) {
						String term2 = another.getPositionSegs().get(idx2).first;
						double weight = Idf.getIdf(term2);
						union += weight;
						idx2++;
					}
					sim = 1.0 * intersect / (union > 0 ? union : 1);
				}
			}
			return sim;
		}

		@Override
		public int getLength() {
			if (positionSegs == null) {
				return 0;
			}
			return positionSegs.size();
		}

	}
}
