package com.orisun.mining.util.math.matrix;

import java.util.HashMap;
import java.util.Map;

public class SparseSymMatrix<T extends Number> {
	int dimension = 0;
	Map<Coordinate, T> map = new HashMap<Coordinate, T>();

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
		Coordinate coordinate = new Coordinate(min, max);
		return map.get(coordinate);
	}

	public void set(int row, int col, T value) {
		int min = row;
		int max = col;
		if (row > col) {
			min = col;
			max = row;
		}
		Coordinate coordinate = new Coordinate(min, max);
		if (!map.containsKey(coordinate)) {
			dimension++;
		}
		map.put(coordinate, value);
	}
}
