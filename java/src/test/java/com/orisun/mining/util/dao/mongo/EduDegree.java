package com.orisun.mining.util.dao.mongo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Author:orisun
 * @Since:2016-4-7
 * @Version:1.0
 */
public enum EduDegree {

	UNLIMITED(0, "不限"), //
	OTHER(1, "其他"), //
	JUNIOR(2, "大专"), //
	UNDERGRADUATE(3, "本科"), //
	MASTER(4, "硕士"), //
	DOCTOR(5, "博士"); //

	/**
	 * 约定：index越大，学历越高
	 */
	private final int index;
	private final String desc;

	private static Map<Integer, EduDegree> indexMap = new HashMap<Integer, EduDegree>();
	private static Map<String, EduDegree> descMap = new HashMap<String, EduDegree>();

	private EduDegree(int i, String desc) {
		this.index = i;
		this.desc = desc;
	}

	static {
		for (EduDegree ele : EduDegree.values()) {
			indexMap.put(ele.getIndex(), ele);
			descMap.put(ele.getDesc(), ele);
		}
	}

	public static EduDegree parseIndex(int i) {
		return indexMap.get(i);
	}

	/**
	 * 解析失败时返回EduDegree.OTHER
	 * 
	 * @param desc
	 * @return
	 */
	public static EduDegree parseDesc(String desc) {
		EduDegree rect = descMap.get(desc);
		if (rect == null) {
			rect = EduDegree.OTHER;
		}
		return rect;
	}

	public int getIndex() {
		return index;
	}

	public String getDesc() {
		return desc;
	}

}
