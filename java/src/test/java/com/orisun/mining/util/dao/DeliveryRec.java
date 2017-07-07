package com.orisun.mining.util.dao;

@DataBase(name = DBName.MYDB)
@Table(name = "delivery_rec")
public class DeliveryRec {
	@Id
	private int id;
	private int positionId;
	private int recPositionId;
	private float recScore;

	public void setId(int id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public long getPositionId() {
		return positionId;
	}

	public void setRecPositionId(int recPositionId) {
		this.recPositionId = recPositionId;
	}

	public int getRecPositionId() {
		return recPositionId;
	}

	public void setRecScore(float recScore) {
		this.recScore = recScore;
	}

	public float getRecScore() {
		return recScore;
	}
}
