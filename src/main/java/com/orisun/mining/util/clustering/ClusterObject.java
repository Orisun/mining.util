package com.orisun.mining.util.clustering;

/**
 * 聚类算法中的一个实例点
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-9
 * @Version:
 */
public class ClusterObject<T extends ClustertInst> {

	int cid = 0; // 类标号
	boolean visited = false; // 该点是否被访问过，专为DBScan提供
	int size = 0; // 如果该点是类簇心，则size表示该簇的大小
	T data;

	public ClusterObject(T data) {
		this.data = data;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
