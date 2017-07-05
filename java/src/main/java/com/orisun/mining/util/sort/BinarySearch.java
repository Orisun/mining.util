package com.orisun.mining.util.sort;

import java.util.List;

public class BinarySearch {

	/**
	 * 二分查找法返回一个元素在列表中的位置，若不存在则返回-1<br>
	 * 如果元素重复出现，则返回第几次出现的位置是不确定的
	 * 
	 * @param list
	 *            必须事先从小到大排好序
	 * @param ele
	 * @return
	 */
	public static <V extends Comparable<V>> int search(List<V> list, V ele) {
		if (list == null || list.size() == 0) {
			return -1;
		}
		int low = 0;
		int high = list.size() - 1;
		while (low <= high) {
			int mid = (low + high) >> 1; // 等价于除2，但位移运算更快
			if (list.get(mid).compareTo(ele) > 0) {
				high = mid - 1;
			} else if (list.get(mid).compareTo(ele) < 0) {
				low = mid + 1;
			} else {
				return mid;
			}
		}
		return -1;
	}

	/**
	 * 二分查找法返回一个元素在列表中的位置，若不存在则返回与之最接近的元素的位置<br>
	 * 如果元素重复出现，则返回第几次出现的位置是不确定的
	 * 
	 * @param list
	 *            必须事先从小到大排好序
	 * @param ele
	 * @return
	 */
	public static <V extends Comparable<V>> int searchNearest(List<V> list, V ele) {
		if (list == null || list.size() == 0) {
			return -1;
		}
		int low = 0;
		int high = list.size() - 1;
		int mid = high;
		while (low <= high) {
			mid = (low + high) >> 1; // 等价于除2，但位移运算更快
			if (list.get(mid).compareTo(ele) > 0) {
				high = mid - 1;
			} else if (list.get(mid).compareTo(ele) < 0) {
				low = mid + 1;
			} else {
				return mid;
			}
		}
		return mid;
	}

	/**
	 * 一个list把实数空间切分成N+1个区间，寻找ele位于哪个区间上
	 * 
	 * @param list
	 *            必须事先排好序
	 * @param ele
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <V extends Comparable> int searchSection(List<V> list, V ele) {
		int mid = 0;
		if (list == null || list.size() == 0) {
			return mid;
		}
		int low = 0;
		int high = list.size() - 1;
		while (low <= high) {
			mid = (low + high) >> 1; // 等价于除2，但位移运算更快
			if (list.get(mid).compareTo(ele) > 0) {
				high = mid - 1;
			} else if (list.get(mid).compareTo(ele) < 0) {
				low = mid + 1;
			} else {
				return mid;
			}
		}
		return low;
	}
}
