package com.orisun.mining.util.dao.mongo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orisun.mining.util.dao.Table;

@MongoDataBase(name = MongoDBName.POSITION_REC)
@Table(name = "user_model")
public class UserModel extends MongoEntity implements Serializable {

	private static final long serialVersionUID = -1021756612263990368L;

	private Integer userid;
	private Boolean ok;
	private Map<String, Double> city;
	private Map<String, Integer> favor;
	private Map<EduDegree, Double> edu;
	private List<EduDegree> cate;
	private Set<EduDegree> bad;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Map<String, Double> getCity() {
		return city;
	}

	public void setCity(Map<String, Double> city) {
		this.city = city;
	}

	public Map<String, Integer> getFavor() {
		return favor;
	}

	public void setFavor(Map<String, Integer> favor) {
		this.favor = favor;
	}

	public Map<EduDegree, Double> getEdu() {
		return edu;
	}

	public void setEdu(Map<EduDegree, Double> edu) {
		this.edu = edu;
	}

	public List<EduDegree> getCate() {
		return cate;
	}

	public void setCate(List<EduDegree> cate) {
		this.cate = cate;
	}

	public Set<EduDegree> getBad() {
		return bad;
	}

	public void setBad(Set<EduDegree> bad) {
		this.bad = bad;
	}

	public Boolean getOk() {
		return ok;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}

}
