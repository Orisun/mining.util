package com.orisun.mining.util.dao.mongo;

public class UserNeighbor {

	private int uid;
	private double sim;

	public UserNeighbor() {

	}

	public UserNeighbor(int uid, double sim) {
		this.uid = uid;
		this.sim = sim;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public double getSim() {
		return sim;
	}

	public void setSim(double sim) {
		this.sim = sim;
	}

}
