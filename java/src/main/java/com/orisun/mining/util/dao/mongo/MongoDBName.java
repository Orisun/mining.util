package com.orisun.mining.util.dao.mongo;

public enum MongoDBName {
	SUGGEST("suggestion"), //
	POSITION_REC("personalrec"), //
	TALENT_REC("talentrec"), //
	COMPANY_MODEL("company_model"), //
	POSITION_MODEL("position_model"), //
	USER_MODEL("user_model"); //

	private final String dbname;

	/**
	 * 库名不能包含/\. "$和null
	 * 
	 * {@see https://docs.mongodb.com/manual/reference/limits/}
	 * @param name
	 * @return
	 */
	public static boolean validDbName(String name) {
		return name != null && !"null".equals(name) && !"".equals(name) && !name.contains("/") && !name.contains("\\")
				&& !name.contains("$") && !name.contains(".") && !name.contains("\"");
	}

	/**
	 * collection名不能包含$，不能是null或空串
	 * 
	 * {@see https://docs.mongodb.com/manual/reference/limits/}
	 * @param name
	 * @return
	 */
	public static boolean validCollectionName(String name) {
		return name != null && !"null".equals(name) && !"".equals(name) && !name.contains("$");
	}

	/**
	 * Field Name不能为空，不能包含.，不能以$开头
	 * 
	 * {@see https://docs.mongodb.com/manual/reference/limits/}
	 * @param name
	 * @return
	 */
	public static boolean validFieldName(String name) {
		return name != null && !"null".equals(name) && !"".equals(name) && !name.contains(".") && !name.startsWith("$");
	}

	MongoDBName(String name) {
		assert validDbName(name);
		this.dbname = name;
	}

	public String getDbname() {
		return dbname;
	}

}
