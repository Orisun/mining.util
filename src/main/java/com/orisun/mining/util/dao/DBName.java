package com.orisun.mining.util.dao;

public enum DBName {
	ZHU_ZHAN("lagou"), //
	RECOMMEND("lagou_rec"), //
	DM_BIZ("lagou_dm_biz"), //
	RESUME("lagou_resume"), //
	IM_RECORD("lagou_im_record"), // 聊天记录
	IM_INFO("lagou_im_info"), // IM基本信息
	SEARCH("lagou_search"), // 搜索
	COMMUNITY("lagou_community"), // 言职社区
	PLUS("lagou_plus"), //
	OPEN_SERVICE("lagou_open_service"),// 开通招聘服务
    LABEL("lagou_lable");

	private final String dbname;

	DBName(String name) {
		this.dbname = name;
	}

	public String getDbname() {
		return dbname;
	}
}