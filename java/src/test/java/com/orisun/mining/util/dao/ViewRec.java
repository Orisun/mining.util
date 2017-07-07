package com.orisun.mining.util.dao;

@DataBase(name = DBName.MYDB)
@Table(name = "view_rec")
public class ViewRec {

	@Id
	private int id;
	private int positionId;
	private int recPositionId;
	private double recScore;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPositionid() {
		return positionId;
	}

	public void setPositionid(int positionId) {
		this.positionId = positionId;
	}

	public int getRecpositionid() {
		return recPositionId;
	}

	public void setRecpositionid(int recPositionId) {
		this.recPositionId = recPositionId;
	}

	public double getRecscore() {
		return recScore;
	}

	public void setRecscore(double recScore) {
		this.recScore = recScore;
	}

}
