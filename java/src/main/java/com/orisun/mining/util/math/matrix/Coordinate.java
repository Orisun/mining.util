package com.orisun.mining.util.math.matrix;

import com.orisun.mining.util.DataTransform;
import com.orisun.mining.util.Pair;

/**
 * 二维坐标
 * 
 * @Author:orisun
 * @Since:2015-7-25
 * @Version:1.0
 */
class Coordinate {
	final byte[] arr;

	/**
	 * 设置坐标
	 * 
	 * @param x
	 * @param y
	 */
	public Coordinate(int x, int y) {
		int diff=y-x;
		int[] brr = new int[] { x, diff };
		// 用vint压缩坐标
		this.arr = DataTransform.vintArrToByteArr(brr);
	}

	/**
	 * 获取坐标
	 * 
	 * @return
	 */
	public Pair<Integer, Integer> getXY() {
		int[] brr = DataTransform.byteArrToVIntArr(arr);
		assert brr.length == 2;
		int x=brr[0];
		int y=x+brr[1];
		return Pair.of(x, y);
	}

	/**
	 * 当hashCode相同且equals()返回true时，map会认为是同一个key
	 */
	@Override
	public int hashCode() {
		Pair<Integer, Integer> xy = getXY();
		int code = 31 * xy.first + xy.second;
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinate) {
			Coordinate other = (Coordinate) obj;
			Pair<Integer, Integer> otherxy = other.getXY();
			Pair<Integer, Integer> thisxy = this.getXY();
			if (otherxy.first == thisxy.first
					&& otherxy.second == thisxy.second) {
				return true;
			}
		}
		return false;
	}

}
