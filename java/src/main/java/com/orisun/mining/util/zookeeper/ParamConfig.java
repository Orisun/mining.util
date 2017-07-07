package com.orisun.mining.util.zookeeper;

import com.orisun.mining.util.dao.*;

@DataBase(name = DBName.MYDB)
@Table(name = "param_config")
public class ParamConfig {

	@Id
	private int id;
	private int logicid;
	@Column("paramvalue")
	private double value;
	private String description;
	private String zkpath;

	public ParamConfig() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLogicid() {
		return logicid;
	}

	public void setLogicid(int logicid) {
		this.logicid = logicid;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZkpath() {
		return zkpath;
	}

	public void setZkpath(String zkpath) {
		this.zkpath = zkpath;
	}

}
