package com.orisun.mining.util.dao.mongo;

import java.util.Date;

/**
 * 如果有Map成员，则map的key不能是Number类型，确保json序列化之后的field name都是带引号的string<br>
 * 如果有Map成员，则只支持HashMap<br>
 * 如果有List成员，则只支持ArrayList和LinkedList<br>
 * 如果有Set成员，则只支持HashSet<br>
 * 
 * @author orisun
 * @date 2016年12月27日
 */
public abstract class MongoEntity {

	private Date updatetime;

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

}
