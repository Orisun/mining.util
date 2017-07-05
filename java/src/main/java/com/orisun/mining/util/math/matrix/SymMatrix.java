package com.orisun.mining.util.math.matrix;

import java.lang.reflect.Array;

/**
 * 对称矩阵可以用一维矩阵来存储，内存消耗量减少一半
 * 
 * @Author:orisun
 * @Since:2015-7-24
 * @Version:1.0
 */
public class SymMatrix<T extends Number> {
	int dimension;
	T[] arr;

	@SuppressWarnings("unchecked")
	public SymMatrix(Class<T> type, int n) {
		dimension = n;
		int len = (1 + n) * n / 2;
		arr = (T[]) Array.newInstance(type, len);// 初始化后所有元素都是null
	}

	public int getDimension() {
		return dimension;
	}

	/**
	 * 注意：如果没有给元素赋过值，get出来是null而不是0
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public T get(int row, int col) {
		int min = row;
		int max = col;
		if (row > col) {
			min = col;
			max = row;
		}
		int k = max * (max + 1) / 2 + min;
		return arr[k];
	}

	public void set(int row, int col, T value) {
		int min = row;
		int max = col;
		if (row > col) {
			min = col;
			max = row;
		}
		int k = max * (max + 1) / 2 + min;
		arr[k] = value;
	}
}
