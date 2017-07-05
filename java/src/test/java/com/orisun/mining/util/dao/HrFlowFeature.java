package com.orisun.mining.util.dao;


@DataBase(name = DBName.RECOMMEND)
@Table(name = "hr_model_dealorder")
public class HrFlowFeature {

	@Id(auto_increment = false)
	private int hrid;
	@Column("to_deal")
	private int toDeal;

	public int getHrid() {
		return hrid;
	}

	public void setHrid(int hrid) {
		this.hrid = hrid;
	}

	public int getToDeal() {
		return toDeal;
	}

	public void setToDeal(int toDeal) {
		this.toDeal = toDeal;
	}

}
