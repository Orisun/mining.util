package com.orisun.mining.util.clustering;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DBScan<T extends ClustertInst> extends BaseCluster<T> {

	private static Log logger = LogFactory.getLog(DBScan.class);
	/**
	 * 核心点的区域半径（距离=1-相似度）
	 */
	private double Eps = 0.3; //
	/**
	 * 核心点邻居数的最少阈值
	 */
	private int MinPts = 2; // 密度

	public DBScan() {

	}

	public DBScan(double Eps) {
		this.Eps = Eps;
	}

	/**
	 * 
	 * @param Eps
	 *            核心点的区域半径（距离=1-相似度）
	 * @param MinPts
	 *            核心点邻居数的最少阈值，邻居数包括核心点自身在内
	 */
	public DBScan(double Eps, int MinPts) {
		this.Eps = Eps;
		this.MinPts = MinPts;
	}

	/**
	 * 从候选集合中选出特定点的邻居。<br>
	 * 由于自己到自己的距离是0,所以自己也是自己的neighbor
	 * 
	 * @param p
	 * @param objects
	 * @return
	 */
	private List<ClusterObject<T>> getNeighbors(ClusterObject<T> p, List<ClusterObject<T>> objects) {
		List<ClusterObject<T>> neighbors = new ArrayList<ClusterObject<T>>();
		for (ClusterObject<T> q : objects) {
			double sim = p.getData().simWith(q.getData());
			if (1 - sim <= Eps) { // 1-相似度即为距离
				neighbors.add(q);
			}
		}
		return neighbors;
	}

	/**
	 * 簇扩张
	 * 
	 * @param p
	 * @param neighbors
	 * @param clusterID
	 * @param objects
	 */
	private void expandCluster(ClusterObject<T> p, List<ClusterObject<T>> neighbors, int clusterID,
			List<ClusterObject<T>> objects) {
		p.setCid(clusterID);
		for (ClusterObject<T> q : neighbors) {
			if (!q.isVisited()) {
				q.setVisited(true);
				List<ClusterObject<T>> qneighbors = getNeighbors(q, objects);
				if (qneighbors.size() >= MinPts) {
					Iterator<ClusterObject<T>> it = qneighbors.iterator();
					while (it.hasNext()) {
						ClusterObject<T> no = it.next();
						if (no.getCid() <= 0)
							no.setCid(clusterID);
					}
				}
			}
			if (q.getCid() <= 0) { // q不是任何簇的成员
				q.setCid(clusterID);
			}
		}
	}

	@Override
	public int cluster(List<ClusterObject<T>> objects) {
		logger.info("data size " + objects.size());
		int clusterID = 0;
		boolean AllVisited = false;
		while (!AllVisited) {
			for (ClusterObject<T> p : objects) {
				if (p.isVisited()) {
					continue;
				}
				AllVisited = false;
				p.setVisited(true); // 设为visited后就已经确定了它是核心点还是边界点
				List<ClusterObject<T>> neighbors = getNeighbors(p, objects);
				if (neighbors.size() < MinPts) {
					if (p.getCid() <= 0)
						p.setCid(-1); // cid初始为0,表示未分类；分类后设置为一个正数；设置为-1表示噪声。
				} else {
					if (p.getCid() <= 0) {
						clusterID++;
						expandCluster(p, neighbors, clusterID, objects);
					} else {
						int iid = p.getCid();
						expandCluster(p, neighbors, iid, objects);
					}
				}
				AllVisited = true;
			}
		}
		return clusterID;
	}

	/**
	 * 预测新的点属于哪个簇。<br>
	 * 返回第一个与dataObject相似度达到阈值的簇心编号。如果与已知的簇心都不是足够的相似，则返回0
	 * 
	 * @param dataObject
	 * @return
	 */
	public ClusterResult attachToCluster(ClusterObject<T> dataObject) {
		if (centers == null || centers.size() == 0) {
			System.err.println("DBScan簇心尚未生成");
		}
		double minDist = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < centers.size(); i++) {
			ClusterObject<T> center = centers.get(i);
			double dist = 1 - center.getData().simWith(dataObject.getData());// 1-相似度即为距离
			if (dist < Eps) {
				if (dist < minDist) {
					minDist = dist;
					index = i;
				}
			}
		}
		if (index >= 0) {
			return new ClusterResult(centers.get(index).getCid(), minDist);
		} else {
			return null;
		}
	}

	public double getEps() {
		return Eps;
	}

	public void setEps(double eps) {
		Eps = eps;
	}

	public int getMinPts() {
		return MinPts;
	}

	public void setMinPts(int minPts) {
		MinPts = minPts;
	}

}