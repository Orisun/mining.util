package com.orisun.mining.util.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @Author:orisun
 * @Since:2015-10-27
 * @Version:1.0
 */
public class ViewRecDao extends BaseDao<ViewRec, Integer> {

	private static Log logger = LogFactory.getLog(ViewRecDao.class);

	public ViewRecDao() throws Exception {
		super();
	}

	public List<Integer> getViewRecPositions(int positionId) {
		List<Integer> rect = new ArrayList<Integer>();
		List<ViewRec> datas = this.getDataByPage("recpositionid,recscore",
				"positionid=" + positionId, 1, 1000);
		if (datas != null && datas.size() > 0) {
			logger.debug("get " + datas.size() + " rec positions of "
					+ positionId + " from view_rec");
			// 从数据库读出推荐列表后，立即按得分降序排列
			Collections.sort(datas, new Comparator<ViewRec>() {
				@Override
				public int compare(ViewRec o1, ViewRec o2) {
					if (o1.getRecscore() > o2.getRecscore()) {
						return -1;
					} else if (o1.getRecscore() < o2.getRecscore()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			for (ViewRec entity : datas) {
				rect.add(entity.getRecpositionid());
			}
		} else {
			logger.debug("get 0 rec positions of " + positionId
					+ " from view_rec");
		}
		return rect;

	}

}
