package com.orisun.mining.util.clustering;

import com.orisun.mining.util.similarity.SimAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 聚类算法的基类
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-9
 * @Version:
 */
public abstract class BaseCluster<T extends ClustertInst> {

	SimAlgorithm distCalculator;// 距离计算器

	public void setDistCalculator(SimAlgorithm distCalculator) {
		this.distCalculator = distCalculator;
	}

	// 簇心的集合
	protected List<ClusterObject<T>> centers = new CopyOnWriteArrayList<ClusterObject<T>>();

	/**
	 * 对一堆点进行聚类,反回簇的个数
	 * 
	 * @param objects
	 * @return
	 */
	public abstract int cluster(List<ClusterObject<T>> objects);

	/**
	 * 聚类完成之后，遍历所有实例点，找出所有簇心。离群点当作簇心对待，只不过它的cid是某个负数
	 * 
	 * @param dataObjects
	 * @return
	 */
	public List<ClusterObject<T>> getClusterCenters(
			List<ClusterObject<T>> dataObjects) {
		List<ClusterObject<T>> centers = new ArrayList<ClusterObject<T>>();
		Map<Integer, List<ClusterObject<T>>> clusters = new HashMap<Integer, List<ClusterObject<T>>>();
		int id = 1;
		for (ClusterObject<T> dataObject : dataObjects) {
			int clusterID = dataObject.getCid();
			if (clusterID == -1) {// 如果没有归到任何簇中，则它自己就是簇心
				dataObject.setCid(0 - id);
				id++;
				centers.add(dataObject);
			} else {
				List<ClusterObject<T>> list = null;
				if (clusters.containsKey(clusterID)) {
					list = clusters.get(clusterID);
				} else {
					list = new ArrayList<ClusterObject<T>>();
				}
				list.add(dataObject);
				clusters.put(clusterID, list);
			}
		}
		for (Entry<Integer, List<ClusterObject<T>>> entry : clusters.entrySet()) {
			List<ClusterObject<T>> objects = entry.getValue();
			ClusterObject<T> center = getCenter(objects);
			center.setSize(entry.getValue().size());
			if (center != null) {
				centers.add(center);
			}
		}

		return centers;
	}

	/**
	 * 从簇中选择一个实例作簇心<br>
	 * 方法：计算每一个实例到其他实例的距离和，距离和最小的实例当簇心
	 * 
	 * @param dataObjects
	 * @return
	 */
	public ClusterObject<T> getCenter(List<ClusterObject<T>> dataObjects) {
		int len = dataObjects.size();
		double[] array = new double[len * (len + 1) / 2];
		int k = 0;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < i; j++) {
				double dist = 1.0 - dataObjects.get(i).getData()
						.simWith(dataObjects.get(j).getData());// 1-相似度即为距离
				array[k++] = dist;
			}
		}

		double min_distsum = Double.MAX_VALUE;
		int bestIndex = -1;
		for (int i = 0; i < len; i++) {
			double distsum = 0.0;
			for (int j = 0; j < len; j++) {
				if (i >= j) {
					distsum += array[i * (i + 1) / 2 + j];
				} else {
					distsum += array[j * (j + 1) / 2 + i];
				}
			}
			if (distsum < min_distsum) {
				min_distsum = distsum;
				bestIndex = i;
			}
		}
		if (bestIndex >= 0) {
			return dataObjects.get(bestIndex);
		} else {
			return null;
		}
	}
}
