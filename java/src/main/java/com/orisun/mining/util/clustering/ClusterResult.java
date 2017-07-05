package com.orisun.mining.util.clustering;

public class ClusterResult {

	private int centerID; // 归属于哪个簇心
	private double dist; // 到簇心的距离

	public ClusterResult() {

	}

	public ClusterResult(int center, double dist) {
		this.centerID = center;
		this.dist = dist;
	}

	public int getCenterID() {
		return centerID;
	}

	public void setCenterID(int centerID) {
		this.centerID = centerID;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

}
