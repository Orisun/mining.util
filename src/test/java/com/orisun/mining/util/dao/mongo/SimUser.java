package com.orisun.mining.util.dao.mongo;

import com.orisun.mining.util.dao.Table;

import java.util.List;

@MongoDataBase(name = MongoDBName.TALENT_REC)
@Table(name = "user_sim")
public class SimUser extends MongoEntity {

	private int userid;
	private List<UserNeighbor> neighbors;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public List<UserNeighbor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<UserNeighbor> neighbors) {
		this.neighbors = neighbors;
	}

}
