package com.orisun.mining.util.dao;  

import java.util.Date;
  
@DataBase(name = DBName.MYDB)
@Table(name = "user_deliver")
public class UserDeliver {

	@Id
	private int id;
	private int userId;
	private int positionId;
	private int companyId;
	private Date deliverTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getPositionId() {
		return positionId;
	}
	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public Date getDeliverTime() {
		return deliverTime;
	}
	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}
	
	
}
