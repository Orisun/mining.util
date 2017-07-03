package com.orisun.mining.util.zookeeper;

/**
 * 
 *@Author:orisun 
 *@Since:2016-4-7  
 *@Version:1.0
 */
public class ZkParam {

	private double value;
	private String path;
	private int logicid;

	public ZkParam(double defaultValue, String path, int logicid) {
		this.value = defaultValue;
		this.path = path;
		this.logicid = logicid;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getLogicid() {
		return logicid;
	}

	public void setLogicid(int logicid) {
		this.logicid = logicid;
	}
}
