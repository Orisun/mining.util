package com.orisun.mining.util.zookeeper;

import com.orisun.mining.util.dao.BaseDao;

import java.util.List;

public class ParamConfigDao extends BaseDao<ParamConfig, Integer> {

	public ParamConfigDao() throws Exception {
		super();
	}

	public ParamConfig getByLogicId(int logicid) {
		List<ParamConfig> list = super.getDataByPage("*", "logicid=" + logicid,
				1, 1);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

}
