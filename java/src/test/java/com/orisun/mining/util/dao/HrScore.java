package com.orisun.mining.util.dao;

import java.sql.Timestamp;

@DataBase(name = DBName.MYDB)
@Table(name = "hr_model_pvscore")
public class HrScore {

	@Id
	@Column("id")
	private int id;
	@Column("hrid")
	private int hrid;
	@Column("total")
	private double total;
	@Column("updatetime")
	private Timestamp updatetime;
	
	public HrScore(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHrid() {
		return hrid;
	}

	public void setHrid(int hrid) {
		this.hrid = hrid;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}

}
