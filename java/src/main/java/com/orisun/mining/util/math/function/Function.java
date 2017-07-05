package com.orisun.mining.util.math.function;

import com.orisun.mining.util.math.Vector;


public abstract class Function {

	protected int dimension;// 自变量的维度

	/**
	 * 给定X求函数值
	 * 
	 * @param X
	 * @return
	 */
	public abstract double getValue(Vector X);

	/**
	 * 给定X求导函数值,由于要对各个自变量求偏导，所以结果是个向量
	 * 
	 * @param X
	 * @return
	 */
	public abstract Vector getGradient(Vector X);

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

}
