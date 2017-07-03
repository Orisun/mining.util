package com.orisun.mining.util.dao;

public enum DBName {
    MYDB("my_table");

	private final String dbname;

	DBName(String name) {
		this.dbname = name;
	}

	public String getDbname() {
		return dbname;
	}
}