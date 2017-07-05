package com.orisun.mining.util.math.matrix;

import java.util.HashMap;
import java.util.Map;

public class SparseMatrix<T extends Number> {
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
		Coordinate coordinate = new Coordinate(row, col);
		return map.get(coordinate);
	}

	public void set(int row, int col, T value) {
		Coordinate coordinate = new Coordinate(row, col);
		if (!map.containsKey(coordinate)) {
			dimension++;
		}
		map.put(coordinate, value);
	}
}
